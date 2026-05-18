package com.masjid.jemaah.feature.event

import com.masjid.core.domain.MasjidEvent
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class EventIntent : BaseIntent {
    data class LoadEvents(val masjidId: String) : EventIntent()
}

sealed class EventState : BaseState {
    object Loading : EventState()
    data class Success(val events: List<MasjidEvent>) : EventState()
    data class Error(val message: String) : EventState()
}
