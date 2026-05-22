package com.masjid.takmir.feature.profile

import com.masjid.core.domain.Masjid
import com.masjid.core.domain.UpdateProfileRequest
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class ProfileIntent : BaseIntent {
    object LoadProfile : ProfileIntent()
    data class UpdateProfile(val request: UpdateProfileRequest) : ProfileIntent()
    data class SelectTheme(val mode: Int) : ProfileIntent()
    object Logout : ProfileIntent()
}

data class ProfileState(
    val profile: Masjid? = null,
    val themeMode: Int = 0,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val error: String? = null
) : BaseState
