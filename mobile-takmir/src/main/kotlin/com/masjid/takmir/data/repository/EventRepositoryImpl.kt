package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateEventRequest
import com.masjid.core.domain.MasjidEvent
import com.masjid.core.domain.UpdateEventRequest
import com.masjid.core.network.TakmirApiClient
import com.masjid.takmir.data.local.EventDao
import com.masjid.takmir.data.local.entity.EventEntity
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val apiClient: TakmirApiClient,
    private val eventDao: EventDao
) : EventRepository {

    override suspend fun getEvents(masjidId: String): AppResult<List<MasjidEvent>> {
        return AppResult.runCatching {
            val events = apiClient.getEvents(masjidId)
            eventDao.deleteAllByMasjidId(masjidId)
            eventDao.insertAll(events.map { it.toEntity(masjidId) })
            events
        }
    }

    override suspend fun createEvent(
        masjidId: String,
        request: CreateEventRequest
    ): AppResult<MasjidEvent> {
        return AppResult.runCatching {
            val event = apiClient.createEvent(masjidId, request)
            eventDao.insert(event.toEntity(masjidId))
            event
        }
    }

    override suspend fun updateEvent(
        masjidId: String,
        eventId: String,
        request: UpdateEventRequest
    ): AppResult<MasjidEvent> {
        return AppResult.runCatching {
            val event = apiClient.updateEvent(masjidId, eventId, request)
            eventDao.insert(event.toEntity(masjidId))
            event
        }
    }

    override suspend fun deleteEvent(masjidId: String, eventId: String): AppResult<Unit> {
        return AppResult.runCatching {
            apiClient.deleteEvent(masjidId, eventId)
            val entity = eventDao.getById(eventId)
            entity?.let { eventDao.delete(it) }
        }
    }

    private fun MasjidEvent.toEntity(masjidId: String) = EventEntity(
        id = id,
        title = title,
        description = description,
        date = date,
        startTime = startTime,
        endTime = endTime,
        location = location,
        masjidId = masjidId
    )
}
