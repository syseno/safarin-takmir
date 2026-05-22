package com.masjid.jemaah.feature.event

import com.masjid.core.domain.MasjidEvent
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class EventIntent : BaseIntent {
    data class LoadEvents(val masjidId: String) : EventIntent()
}

data class EventState(
    val events: List<MasjidEvent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) : BaseState
