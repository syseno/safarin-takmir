package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.MasjidEvent
import com.masjid.takmir.data.repository.EventRepository
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(masjidId: String): AppResult<List<MasjidEvent>> {
        return eventRepository.getEvents(masjidId)
    }
}
