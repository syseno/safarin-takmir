package com.masjid.jemaah.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.PublicFinanceResponse
import com.masjid.core.network.PublicApiClient
import javax.inject.Inject

class FinanceRepositoryImpl @Inject constructor(
    private val publicApiClient: PublicApiClient
) : FinanceRepository {
    override suspend fun getFinances(masjidId: String, page: Int, limit: Int): AppResult<PublicFinanceResponse> {
        return AppResult.runCatching {
            publicApiClient.getMasjidFinance(masjidId, page, limit)
        }
    }
}
