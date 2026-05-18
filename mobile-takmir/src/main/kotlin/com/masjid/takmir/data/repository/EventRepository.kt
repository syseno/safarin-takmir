package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateEventRequest
import com.masjid.core.domain.MasjidEvent
import com.masjid.core.domain.UpdateEventRequest

/**
 * Event repository interface — DIP enforced.
 */
interface EventRepository {
    suspend fun getEvents(masjidId: String): AppResult<List<MasjidEvent>>
    suspend fun createEvent(masjidId: String, request: CreateEventRequest): AppResult<MasjidEvent>
    suspend fun updateEvent(masjidId: String, eventId: String, request: UpdateEventRequest): AppResult<MasjidEvent>
    suspend fun deleteEvent(masjidId: String, eventId: String): AppResult<Unit>
}
