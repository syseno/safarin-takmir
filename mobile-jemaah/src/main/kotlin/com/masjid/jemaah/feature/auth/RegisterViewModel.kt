package com.masjid.jemaah.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.RegisterRequest
import com.masjid.core.network.AuthApiClient
import com.masjid.jemaah.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterState {
    object Idle : RegisterState()
    object Submitting : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authApiClient: AuthApiClient,
    private val tokenManager: EncryptedTokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            // Client-side validation
            if (name.isBlank()) { _state.value = RegisterState.Error("Nama tidak boleh kosong"); return@launch }
            if (email.isBlank()) { _state.value = RegisterState.Error("Email tidak boleh kosong"); return@launch }
            if (password.length < 6) { _state.value = RegisterState.Error("Password minimal 6 karakter"); return@launch }
            if (password != confirmPassword) { _state.value = RegisterState.Error("Password tidak cocok"); return@launch }

            _state.value = RegisterState.Submitting
            val result = AppResult.runCatching {
                authApiClient.register(RegisterRequest(name = name, email = email, password = password))
            }
            when (result) {
                is AppResult.Success -> {
                    tokenManager.saveToken(result.data.token)
                    tokenManager.saveUserRole(result.data.user.role)
                    _state.value = RegisterState.Success
                }
                is AppResult.Error -> _state.value = RegisterState.Error(result.message)
            }
        }
    }
}
