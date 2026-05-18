package com.masjid.jemaah.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.MasjidEvent

interface EventRepository {
    suspend fun getEvents(masjidId: String): AppResult<List<MasjidEvent>>
}
