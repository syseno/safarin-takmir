package com.masjid.takmir.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.DailyAdzanSchedule
import com.masjid.takmir.core.RefreshManager
import com.masjid.takmir.data.repository.PrayerRepository
import com.masjid.takmir.data.repository.ProfileRepository
import com.masjid.takmir.domain.usecase.GetDashboardUseCase
import com.masjid.takmir.location.LocationProvider
import com.masjid.takmir.security.EncryptedTokenManager
import com.masjid.takmir.service.AdzanScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase,
    private val tokenManager: EncryptedTokenManager,
    private val refreshManager: RefreshManager,
    private val prayerRepository: PrayerRepository,
    private val locationProvider: LocationProvider,
    private val adzanScheduler: AdzanScheduler,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _countdown = MutableStateFlow("")
    val countdown: StateFlow<String> = _countdown.asStateFlow()

    private val _localAdzanSchedules = MutableStateFlow<List<DailyAdzanSchedule>>(emptyList())
    private val _localCity = MutableStateFlow<String?>(null)
    private val _localProvince = MutableStateFlow<String?>(null)

    init {
        observeRefresh()
        loadAdzanSchedule()
        startCountdown()
        fetchDashboard()
        refreshWithLocation()
    }

    private fun observeRefresh() {
        viewModelScope.launch {
            refreshManager.refreshEvent.collect {
                fetchDashboard()
                refreshWithLocation()
            }
        }
    }

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboard -> {
                fetchDashboard()
                refreshWithLocation()
            }
            is DashboardIntent.Refresh -> {
                fetchDashboard()
                refreshWithLocation()
            }
        }
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

            _localCity.value = city
            _localProvince.value = province

            val currentState = _state.value
            if (currentState is DashboardState.Success) {
                _state.value = currentState.copy(
                    currentCity = city,
                    currentProvince = province
                )
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
                val finalSchedules = if (today != null && findNextPrayer(today) == null && tomorrow != null) {
                    updatedSchedules.map { schedule ->
                        if (schedule == tomorrow) {
                            schedule.copy(times = schedule.times.map { 
                                it.copy(isNext = it.name == "Subuh")
                            })
                        } else schedule
                    }
                } else {
                    updatedSchedules
                }

                _localAdzanSchedules.value = finalSchedules

                val currentState = _state.value
                if (currentState is DashboardState.Success) {
                    _state.value = currentState.copy(adzanSchedules = finalSchedules)
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
                val schedules = _localAdzanSchedules.value
                val schedule = schedules.find { it.dayLabel == "Hari Ini" }
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

    private fun fetchDashboard() {
        viewModelScope.launch {
            _state.value = DashboardState.Loading
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.value = DashboardState.Error("Masjid ID tidak ditemukan")
                return@launch
            }
            
            coroutineScope {
                val profileDeferred = async { profileRepository.getProfile(masjidId) }
                val dashboardDeferred = async { getDashboardUseCase(masjidId) }
                
                val profileResult = profileDeferred.await()
                val dashboardResult = dashboardDeferred.await()
                
                if (dashboardResult is AppResult.Success) {
                    val data = dashboardResult.data
                    val masjidName = if (profileResult is AppResult.Success) profileResult.data.name else ""
                    
                    _state.value = DashboardState.Success(
                        totalSaldo = data.finance.balance.toLong(),
                        totalIncome = data.finance.totalIncome.toLong(),
                        totalExpense = data.finance.totalExpense.toLong(),
                        recentTransactions = data.recentFinance,
                        upcomingEvents = data.upcomingEvents,
                        donationSummary = data.donations,
                        inventoryTotal = data.inventory.totalItems,
                        masjidName = masjidName,
                        adzanSchedules = _localAdzanSchedules.value,
                        currentCity = _localCity.value,
                        currentProvince = _localProvince.value
                    )
                } else if (dashboardResult is AppResult.Error) {
                    _state.value = DashboardState.Error(dashboardResult.message)
                }
            }
        }
    }
}
