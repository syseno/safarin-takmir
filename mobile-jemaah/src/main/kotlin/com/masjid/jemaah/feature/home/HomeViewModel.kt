package com.masjid.jemaah.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.DailyAdzanSchedule
import com.masjid.jemaah.data.repository.PrayerRepository
import com.masjid.jemaah.domain.usecase.SearchMasjidUseCase
import com.masjid.jemaah.location.LocationProvider
import com.masjid.jemaah.service.AdzanScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchMasjidUseCase: SearchMasjidUseCase,
    private val getNearestMasjidsUseCase: com.masjid.jemaah.domain.usecase.GetNearestMasjidsUseCase,
    private val prayerRepository: PrayerRepository,
    private val locationProvider: LocationProvider,
    private val adzanScheduler: AdzanScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _countdown = MutableStateFlow("")
    val countdown: StateFlow<String> = _countdown.asStateFlow()

    init {
        loadAdzanSchedule()
        handleIntent(HomeIntent.LoadInitial)
        startCountdown()
        refreshWithLocation()
    }

    fun refreshWithLocation() {
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            val lat = location?.latitude ?: -6.200000
            val lng = location?.longitude ?: 106.816666

            // 1. Refresh prayer schedule
            prayerRepository.refreshPrayerSchedule(lat, lng)

            // 2. Fetch location names
            val (city, province) = if (location != null) {
                locationProvider.getLocationNames(lat, lng)
            } else {
                Pair("Jakarta Pusat", "DKI Jakarta")
            }

            // 3. Fetch nearest masjids (1km radius, limit 3)
            val nearestResult = getNearestMasjidsUseCase(latitude = lat, longitude = lng, radius = 1.0, limit = 3)
            val nearest = if (nearestResult is AppResult.Success) nearestResult.data else emptyList()

            // 4. Resolve the cityId of the closest masjid to the user (without radius constraint)
            val closestResult = getNearestMasjidsUseCase(latitude = lat, longitude = lng, limit = 1)
            val closestCityId = if (closestResult is AppResult.Success) closestResult.data.firstOrNull()?.city?.id else null

            _state.value = _state.value.copy(
                currentCity = city,
                currentProvince = province,
                currentLatitude = lat,
                currentLongitude = lng,
                nearestMasjids = nearest,
                cityId = closestCityId
            )
        }
    }

    private fun loadAdzanSchedule() {
        viewModelScope.launch {
            prayerRepository.getPrayerSchedule().collect { schedules ->
                val today = schedules.find { it.dayLabel == "Hari Ini" } ?: schedules.firstOrNull()
                val tomorrow = schedules.find { it.dayLabel == "Besok" }
                
                val updatedSchedules = schedules.map { schedule ->
                    val nextPrayer = findNextPrayer(schedule)
                    schedule.copy(times = schedule.times.map { 
                        it.copy(isNext = it.name == nextPrayer?.name && schedule == today)
                    })
                }
                
                // If today is finished, mark tomorrow's Subuh as next
                if (today != null && findNextPrayer(today) == null && tomorrow != null) {
                    val finalSchedules = updatedSchedules.map { schedule ->
                        if (schedule == tomorrow) {
                            schedule.copy(times = schedule.times.map { 
                                it.copy(isNext = it.name == "Subuh")
                            })
                        } else schedule
                    }
                    _state.value = _state.value.copy(adzanSchedules = finalSchedules)
                } else {
                    _state.value = _state.value.copy(adzanSchedules = updatedSchedules)
                }

                if (today != null) {
                    adzanScheduler.scheduleAdzan(today)
                }
            }
        }
    }

    private fun findNextPrayer(schedule: DailyAdzanSchedule): com.masjid.core.domain.AdzanTime? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val now = Date()
        return schedule.times.filter { it.name != "Terbit" }
            .find { 
                val prayerTime = sdf.parse("${schedule.date} ${it.time}")
                prayerTime?.after(now) == true
            }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (isActive) {
                val schedule = _state.value.adzanSchedules.find { it.dayLabel == "Hari Ini" }
                if (schedule != null) {
                    val next = findNextPrayer(schedule)
                    if (next != null) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val prayerTime = sdf.parse("${schedule.date} ${next.time}")
                        if (prayerTime != null) {
                            val diff = prayerTime.time - System.currentTimeMillis()
                            
                            val hours = diff / (1000 * 60 * 60)
                            val minutes = (diff / (1000 * 60)) % 60
                            val seconds = (diff / 1000) % 60
                            
                            _countdown.value = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                        }
                    } else {
                        _countdown.value = "--:--:--"
                    }
                }
                delay(1000)
            }
        }
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SubmitSearch -> performSearch(intent.query)
            is HomeIntent.LoadInitial -> performSearch(null)
        }
    }

    private fun performSearch(query: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = searchMasjidUseCase(query)) {
                is AppResult.Success -> {
                    val currentLat = _state.value.currentLatitude
                    val currentLng = _state.value.currentLongitude
                    val masjidsWithDistance = if (currentLat != null && currentLng != null) {
                        result.data.map { masjid ->
                            val lat = masjid.latitude
                            val lng = masjid.longitude
                            if (lat != null && lng != null) {
                                val dist = calculateDistance(currentLat, currentLng, lat, lng)
                                masjid.copy(distance = dist)
                            } else {
                                masjid
                            }
                        }.sortedBy { it.distance ?: Double.MAX_VALUE }
                    } else {
                        result.data
                    }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        masjids = masjidsWithDistance
                    )
                }
                is AppResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth's radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
