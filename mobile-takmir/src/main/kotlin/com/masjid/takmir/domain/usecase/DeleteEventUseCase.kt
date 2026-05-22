package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.takmir.data.repository.EventRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(masjidId: String, eventId: String, deleteType: String = "SINGLE"): AppResult<Unit> {
        return eventRepository.deleteEvent(masjidId, eventId, deleteType)
    }

}
