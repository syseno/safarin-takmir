package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateFinanceRequest
import com.masjid.core.domain.Finance
import com.masjid.takmir.data.repository.FinanceRepository
import javax.inject.Inject

class CreateFinanceUseCase @Inject constructor(
    private val financeRepository: FinanceRepository
) {
    suspend operator fun invoke(masjidId: String, request: CreateFinanceRequest): AppResult<Finance> {
        if (request.title.isBlank()) return AppResult.Error("Judul tidak boleh kosong")
        if (request.amount <= 0) return AppResult.Error("Jumlah harus lebih dari 0")
        return financeRepository.createFinance(masjidId, request)
    }
}
