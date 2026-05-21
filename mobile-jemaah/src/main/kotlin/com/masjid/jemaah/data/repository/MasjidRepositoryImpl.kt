package com.masjid.jemaah.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Masjid
import com.masjid.core.network.PublicApiClient
import com.masjid.jemaah.data.local.MasjidDao
import com.masjid.jemaah.data.local.entity.CachedMasjidEntity
import javax.inject.Inject

class MasjidRepositoryImpl @Inject constructor(
    private val publicApiClient: PublicApiClient,
    private val masjidDao: MasjidDao
) : MasjidRepository {

    override suspend fun searchMasjids(query: String?): AppResult<List<Masjid>> {
        return AppResult.runCatching {
            // PublicApiClient.searchMasjid returns SearchMasjidResponse { masjids, pagination }
            val response = publicApiClient.searchMasjid(query = query, page = 1, limit = 20)
            val masjids = response.masjids
            // Cache results for offline access
            masjidDao.clearAll()
            masjidDao.insertAll(masjids.map { it.toEntity() })
            masjids
        }
    }

    override suspend fun getMasjidDetail(id: String): AppResult<Masjid> {
        return AppResult.runCatching {
            publicApiClient.getMasjidDetail(id)
        }
    }

    private fun Masjid.toEntity() = CachedMasjidEntity(
        id = id,
        name = name,
        description = description,
        addressDetail = addressDetail,
        city = city?.name,
        isFavorite = false
    )
}
