package com.masjid.takmir.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.takmir.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.SubmitLogin -> performLogin(intent.email, intent.password)
            is LoginIntent.ClearError -> _state.value = LoginState.Idle
        }
    }

    private fun performLogin(email: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            when (val result = loginUseCase(email, password)) {
                is AppResult.Success -> _state.value = LoginState.Success
                is AppResult.Error -> _state.value = LoginState.Error(result.message)
            }
        }
    }
}
