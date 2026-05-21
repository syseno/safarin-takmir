package com.masjid.jemaah.feature.search

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
class SearchViewModel @Inject constructor(
    private val searchMasjidUseCase: SearchMasjidUseCase,
    private val prayerRepository: PrayerRepository,
    private val locationProvider: LocationProvider,
    private val adzanScheduler: AdzanScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _countdown = MutableStateFlow("")
    val countdown: StateFlow<String> = _countdown.asStateFlow()

    init {
        loadAdzanSchedule()
        handleIntent(SearchIntent.LoadInitial)
        startCountdown()
        refreshWithLocation()
    }

    fun refreshWithLocation() {
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            if (location != null) {
                prayerRepository.refreshPrayerSchedule(location.latitude, location.longitude)
            } else {
                // Fallback to a default location (e.g. Jakarta) if no GPS available
                prayerRepository.refreshPrayerSchedule(-6.200000, 106.816666)
            }
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

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.SubmitSearch -> performSearch(intent.query)
            is SearchIntent.LoadInitial -> performSearch(null)
        }
    }

    private fun performSearch(query: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = searchMasjidUseCase(query)) {
                is AppResult.Success -> {
                    _state.value = _state.value.copy(isLoading = false, masjids = result.data)
                }
                is AppResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }
}
