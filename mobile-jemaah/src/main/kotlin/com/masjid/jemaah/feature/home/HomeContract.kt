package com.masjid.jemaah.feature.home

import com.masjid.core.domain.DailyAdzanSchedule
import com.masjid.core.domain.Masjid
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class HomeIntent : BaseIntent {
    data class SubmitSearch(val query: String) : HomeIntent()
    object LoadInitial : HomeIntent()
}

data class HomeState(
    val masjids: List<Masjid> = emptyList(),
    val nearestMasjids: List<Masjid> = emptyList(),
    val adzanSchedules: List<DailyAdzanSchedule> = emptyList(),
    val currentCity: String? = null,
    val currentProvince: String? = null,
    val currentLatitude: Double? = null,
    val currentLongitude: Double? = null,
    val cityId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) : BaseState
