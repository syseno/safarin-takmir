package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateFinanceRequest
import com.masjid.core.domain.Finance
import com.masjid.core.domain.UpdateFinanceRequest
import com.masjid.core.network.TakmirApiClient
import com.masjid.takmir.data.local.TransactionDao
import com.masjid.takmir.data.local.entity.TransactionEntity
import javax.inject.Inject

class FinanceRepositoryImpl @Inject constructor(
    private val apiClient: TakmirApiClient,
    private val transactionDao: TransactionDao
) : FinanceRepository {

    override suspend fun getFinances(masjidId: String): AppResult<List<Finance>> {
        return AppResult.runCatching {
            val finances = apiClient.getFinances(masjidId)
            // Cache to Room
            transactionDao.deleteAllByMasjidId(masjidId)
            transactionDao.insertAll(finances.map { it.toEntity(masjidId) })
            finances
        }
    }

    override suspend fun createFinance(
        masjidId: String,
        request: CreateFinanceRequest
    ): AppResult<Finance> {
        return AppResult.runCatching {
            val finance = apiClient.createFinance(masjidId, request)
            transactionDao.insert(finance.toEntity(masjidId))
            finance
        }
    }

    override suspend fun updateFinance(
        masjidId: String,
        financeId: String,
        request: UpdateFinanceRequest
    ): AppResult<Finance> {
        return AppResult.Error("Pembaruan transaksi tidak didukung. Silakan hapus dan buat transaksi baru.")
    }

    override suspend fun deleteFinance(masjidId: String, financeId: String): AppResult<Unit> {
        return AppResult.runCatching {
            apiClient.deleteFinance(masjidId, financeId)
            val entity = transactionDao.getById(financeId)
            entity?.let { transactionDao.delete(it) }
        }
    }

    private fun Finance.toEntity(masjidId: String) = TransactionEntity(
        id = id,
        title = title,
        amount = amount.toLong(),
        type = type,
        description = description,
        createdAt = createdAt,
        masjidId = masjidId
    )
}
