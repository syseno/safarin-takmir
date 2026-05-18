package com.masjid.takmir.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.UpdateProfileRequest
import com.masjid.takmir.domain.usecase.GetProfileUseCase
import com.masjid.takmir.domain.usecase.LogoutUseCase
import com.masjid.takmir.domain.usecase.UpdateProfileUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: EncryptedTokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> loadProfile()
            is ProfileIntent.UpdateProfile -> updateProfile(intent.request)
            is ProfileIntent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.value = ProfileState.Error("Masjid ID tidak ditemukan")
                return@launch
            }
            when (val result = getProfileUseCase(masjidId)) {
                is AppResult.Success -> {
                    _state.value = ProfileState.Success(result.data)
                }
                is AppResult.Error -> {
                    _state.value = ProfileState.Error(result.message)
                }
            }
        }
    }

    private fun updateProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            val masjidId = tokenManager.getMasjidId() ?: return@launch
            when (val result = updateProfileUseCase(masjidId, request)) {
                is AppResult.Success -> {
                    _state.value = ProfileState.Success(result.data)
                }
                is AppResult.Error -> {
                    _state.value = ProfileState.Error(result.message)
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.value = ProfileState.LoggedOut
        }
    }
}
