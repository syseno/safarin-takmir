package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateFinanceRequest
import com.masjid.core.domain.Finance
import com.masjid.core.domain.UpdateFinanceRequest

/**
 * Finance repository interface — DIP enforced.
 */
interface FinanceRepository {
    suspend fun getFinances(masjidId: String): AppResult<List<Finance>>
    suspend fun createFinance(masjidId: String, request: CreateFinanceRequest): AppResult<Finance>
    suspend fun updateFinance(masjidId: String, financeId: String, request: UpdateFinanceRequest): AppResult<Finance>
    suspend fun deleteFinance(masjidId: String, financeId: String): AppResult<Unit>
}
