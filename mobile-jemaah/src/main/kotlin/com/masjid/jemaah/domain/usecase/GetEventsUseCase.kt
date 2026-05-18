package com.masjid.jemaah.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.MasjidEvent
import com.masjid.jemaah.data.repository.EventRepository
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(masjidId: String): AppResult<List<MasjidEvent>> {
        return eventRepository.getEvents(masjidId)
    }
}
