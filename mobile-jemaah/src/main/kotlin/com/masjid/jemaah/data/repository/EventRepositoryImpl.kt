package com.masjid.jemaah.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.MasjidEvent
import com.masjid.core.network.PublicApiClient
import com.masjid.jemaah.data.local.EventDao
import com.masjid.jemaah.data.local.entity.EventEntity
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val publicApiClient: PublicApiClient,
    private val eventDao: EventDao
) : EventRepository {
    override suspend fun getEvents(masjidId: String): AppResult<List<MasjidEvent>> {
        return AppResult.runCatching {
            // getMasjidEvents returns PublicEventsResponse { masjid, events }
            val response = publicApiClient.getMasjidEvents(masjidId)
            val events = response.events
            eventDao.deleteByMasjid(masjidId)
            eventDao.insertAll(events.map { it.toEntity(masjidId) })
            events
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
