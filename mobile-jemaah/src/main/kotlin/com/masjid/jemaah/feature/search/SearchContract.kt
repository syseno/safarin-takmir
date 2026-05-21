package com.masjid.jemaah.feature.search

import com.masjid.core.domain.DailyAdzanSchedule
import com.masjid.core.domain.Masjid
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class SearchIntent : BaseIntent {
    data class SubmitSearch(val query: String) : SearchIntent()
    object LoadInitial : SearchIntent()
}

data class SearchState(
    val masjids: List<Masjid> = emptyList(),
    val adzanSchedules: List<DailyAdzanSchedule> = emptyList(),
    val currentCity: String? = null,
    val currentProvince: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) : BaseState
