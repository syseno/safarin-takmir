package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.MasjidEvent
import com.masjid.core.domain.UpdateEventRequest
import com.masjid.takmir.data.repository.EventRepository
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(
        masjidId: String,
        eventId: String,
        request: UpdateEventRequest
    ): AppResult<MasjidEvent> {
        return eventRepository.updateEvent(masjidId, eventId, request)
    }
}
