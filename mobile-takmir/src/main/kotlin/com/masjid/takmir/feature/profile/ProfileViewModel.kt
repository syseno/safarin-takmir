package com.masjid.takmir.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.UpdateProfileRequest
import com.masjid.takmir.data.local.SettingsManager
import com.masjid.takmir.data.repository.PrayerRepository
import com.masjid.takmir.domain.usecase.GetProfileUseCase
import com.masjid.takmir.domain.usecase.LogoutUseCase
import com.masjid.takmir.domain.usecase.UpdateProfileUseCase
import com.masjid.takmir.location.LocationProvider
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val prayerRepository: PrayerRepository,
    private val tokenManager: EncryptedTokenManager,
    private val settingsManager: SettingsManager,
    private val locationProvider: LocationProvider,
    private val refreshManager: com.masjid.takmir.core.RefreshManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        observeSettings()
        handleIntent(ProfileIntent.LoadProfile)
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsManager.themeMode.collectLatest { mode ->
                _state.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            settingsManager.prayerMethod.collectLatest { methodId ->
                _state.update { it.copy(selectedMethodId = methodId) }
            }
        }
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> loadProfileData()
            is ProfileIntent.UpdateProfile -> updateProfile(intent.request)
            is ProfileIntent.SelectTheme -> selectTheme(intent.mode)
            is ProfileIntent.SelectPrayerMethod -> selectPrayerMethod(intent.methodId)
            is ProfileIntent.Logout -> logout()
        }
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            // 1. Load User Profile
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.update { it.copy(isLoading = false, error = "Masjid ID tidak ditemukan") }
                return@launch
            }
            val profileResult = getProfileUseCase(masjidId)
            if (profileResult is AppResult.Success) {
                _state.update { it.copy(profile = profileResult.data) }
            } else if (profileResult is AppResult.Error) {
                _state.update { it.copy(error = profileResult.message) }
            }

            // 2. Load Prayer Methods list
            val methods = prayerRepository.getPrayerMethods()
            if (methods.isNotEmpty()) {
                _state.update { it.copy(
                    prayerMethods = methods,
                    isLoading = false
                ) }
            } else {
                _state.update { it.copy(
                    isLoading = false,
                    error = _state.value.error ?: "Gagal memuat metode perhitungan adzan."
                ) }
            }
        }
    }

    private fun updateProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val masjidId = tokenManager.getMasjidId() ?: return@launch
            when (val result = updateProfileUseCase(masjidId, request)) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false, profile = result.data) }
                }
                is AppResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    private fun selectTheme(mode: Int) {
        viewModelScope.launch {
            settingsManager.setThemeMode(mode)
        }
    }

    private fun selectPrayerMethod(methodId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isMethodLoading = true) }
            // Save the setting
            settingsManager.setPrayerMethod(methodId)
            
            // Refetch prayer times using coordinates
            try {
                val location = locationProvider.getCurrentLocation()
                val lat = location?.latitude ?: -6.200000
                val lng = location?.longitude ?: 106.816666
                prayerRepository.refreshPrayerSchedule(lat, lng)
                refreshManager.triggerRefresh(com.masjid.takmir.core.RefreshType.DASHBOARD)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _state.update { it.copy(isMethodLoading = false) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.update { it.copy(isLoggedOut = true) }
        }
    }
}
