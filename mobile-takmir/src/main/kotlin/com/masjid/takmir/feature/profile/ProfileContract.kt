package com.masjid.takmir.feature.profile

import com.masjid.core.domain.Masjid
import com.masjid.core.domain.UpdateProfileRequest
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class ProfileIntent : BaseIntent {
    object LoadProfile : ProfileIntent()
    data class UpdateProfile(val request: UpdateProfileRequest) : ProfileIntent()
    object Logout : ProfileIntent()
}

sealed class ProfileState : BaseState {
    object Loading : ProfileState()
    data class Success(val profile: Masjid) : ProfileState()
    data class Error(val message: String) : ProfileState()
    object LoggedOut : ProfileState()
}
