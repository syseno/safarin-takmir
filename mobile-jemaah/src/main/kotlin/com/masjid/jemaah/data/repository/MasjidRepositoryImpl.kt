package com.masjid.jemaah.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Masjid
import com.masjid.core.network.PublicApiClient
import com.masjid.jemaah.data.local.MasjidDao
import com.masjid.jemaah.data.local.entity.MasjidEntity
import javax.inject.Inject

class MasjidRepositoryImpl @Inject constructor(
    private val publicApiClient: PublicApiClient,
    private val masjidDao: MasjidDao
) : MasjidRepository {

    override suspend fun searchMasjid(query: String): AppResult<List<Masjid>> {
        return AppResult.runCatching {
            // PublicApiClient.searchMasjid returns SearchMasjidResponse { masjids, pagination }
            val response = publicApiClient.searchMasjid(query = query, page = 1, limit = 20)
            val masjids = response.masjids
            // Cache results for offline access
            masjidDao.deleteAll()
            masjidDao.insertAll(masjids.map { it.toEntity() })
            masjids
        }
    }

    override suspend fun getMasjidDetail(masjidId: String): AppResult<Masjid> {
        return AppResult.runCatching {
            publicApiClient.getMasjidDetail(masjidId)
        }
    }

    private fun Masjid.toEntity() = MasjidEntity(
        id = id,
        name = name,
        addressDetail = addressDetail,
        city = city?.name,
        verified = verified,
        phone = phone,
        description = description,
        imageUrl = imageUrl
    )
}
