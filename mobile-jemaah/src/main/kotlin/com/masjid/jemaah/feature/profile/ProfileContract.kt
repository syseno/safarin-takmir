package com.masjid.jemaah.feature.profile

import com.masjid.core.domain.PrayerMethodItem
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class ProfileIntent : BaseIntent {
    object LoadProfile : ProfileIntent()
    data class SelectPrayerMethod(val methodId: Int) : ProfileIntent()
    data class SelectTheme(val mode: Int) : ProfileIntent()
    object Logout : ProfileIntent()
}

data class ProfileState(
    val email: String = "",
    val name: String = "",
    val selectedMethodId: Int = 20,
    val themeMode: Int = 0, // 0: System, 1: Light, 2: Dark
    val prayerMethods: Map<String, PrayerMethodItem> = emptyMap(),
    val isLoading: Boolean = false,
    val isMethodLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val error: String? = null
) : BaseState
