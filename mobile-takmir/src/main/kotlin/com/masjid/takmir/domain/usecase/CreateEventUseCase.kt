package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateEventRequest
import com.masjid.core.domain.MasjidEvent
import com.masjid.takmir.data.repository.EventRepository
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(masjidId: String, request: CreateEventRequest): AppResult<MasjidEvent> {
        if (request.title.isBlank()) return AppResult.Error("Judul event tidak boleh kosong")
        if (request.date.isBlank()) return AppResult.Error("Tanggal tidak boleh kosong")
        return eventRepository.createEvent(masjidId, request)
    }
}
