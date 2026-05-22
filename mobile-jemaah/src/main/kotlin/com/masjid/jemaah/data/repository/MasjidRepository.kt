package com.masjid.jemaah.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Masjid

interface MasjidRepository {
    suspend fun searchMasjids(query: String?): AppResult<List<Masjid>>
    suspend fun getMasjidDetail(id: String): AppResult<Masjid>
    suspend fun getNearestMasjids(
        latitude: Double,
        longitude: Double,
        radius: Double? = null,
        limit: Int? = null,
        cityId: String? = null
    ): AppResult<List<Masjid>>
}
