package com.masjid.jemaah.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.jemaah.data.local.SettingsManager
import com.masjid.jemaah.data.repository.AuthRepository
import com.masjid.jemaah.data.repository.PrayerRepository
import com.masjid.jemaah.location.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val prayerRepository: PrayerRepository,
    private val settingsManager: SettingsManager,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        observeSettings()
        handleIntent(ProfileIntent.LoadProfile)
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsManager.prayerMethod.collectLatest { methodId ->
                _state.value = _state.value.copy(selectedMethodId = methodId)
            }
        }
        viewModelScope.launch {
            settingsManager.themeMode.collectLatest { mode ->
                _state.value = _state.value.copy(themeMode = mode)
            }
        }
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> loadProfileData()
            is ProfileIntent.SelectPrayerMethod -> selectPrayerMethod(intent.methodId)
            is ProfileIntent.SelectTheme -> selectTheme(intent.mode)
            is ProfileIntent.Logout -> logout()
        }
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            // 1. Load User Profile
            val profileResult = authRepository.getProfile()
            if (profileResult is AppResult.Success) {
                val user = profileResult.data
                _state.value = _state.value.copy(
                    email = user.email,
                    name = user.name
                )
            } else if (profileResult is AppResult.Error) {
                _state.value = _state.value.copy(error = profileResult.message)
            }

            // 2. Load Prayer Methods list
            val methods = prayerRepository.getPrayerMethods()
            if (methods.isNotEmpty()) {
                _state.value = _state.value.copy(
                    prayerMethods = methods,
                    isLoading = false
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = _state.value.error ?: "Gagal memuat metode perhitungan adzan."
                )
            }
        }
    }

    private fun selectPrayerMethod(methodId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isMethodLoading = true)
            // Save the setting
            settingsManager.setPrayerMethod(methodId)
            
            // Refetch prayer times using coordinates
            try {
                val location = locationProvider.getCurrentLocation()
                if (location != null) {
                    prayerRepository.refreshPrayerSchedule(location.latitude, location.longitude)
                } else {
                    // Fallback to Jakarta
                    prayerRepository.refreshPrayerSchedule(-6.200000, 106.816666)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _state.value = _state.value.copy(isMethodLoading = false)
            }
        }
    }

    private fun selectTheme(mode: Int) {
        viewModelScope.launch {
            settingsManager.setThemeMode(mode)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = _state.value.copy(isLoggedOut = true)
        }
    }
}
