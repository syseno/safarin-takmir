package com.masjid.jemaah.feature.auth

import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class LoginIntent : BaseIntent {
    data class SubmitLogin(val email: String, val password: String) : LoginIntent()
    object ClearError : LoginIntent()
}

sealed class LoginState : BaseState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
