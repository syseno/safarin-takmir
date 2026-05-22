package com.masjid.takmir.feature.event

import com.masjid.core.domain.MasjidEvent
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class EventIntent : BaseIntent {
    data class LoadEvents(val masjidId: String) : EventIntent()
    data class DeleteEvent(val eventId: String, val deleteType: String = "SINGLE") : EventIntent()
    object Refresh : EventIntent()
}

sealed class EventState : BaseState {
    object Loading : EventState()
    data class Success(val events: List<MasjidEvent>) : EventState()
    data class Error(val message: String) : EventState()
}
