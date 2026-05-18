package com.masjid.jemaah.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.PublicFinanceResponse

interface FinanceRepository {
    suspend fun getFinances(masjidId: String, page: Int = 1, limit: Int = 20): AppResult<PublicFinanceResponse>
}
