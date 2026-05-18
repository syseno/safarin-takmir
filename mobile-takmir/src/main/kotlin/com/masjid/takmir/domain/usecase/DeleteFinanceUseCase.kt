package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.takmir.data.repository.FinanceRepository
import javax.inject.Inject

class DeleteFinanceUseCase @Inject constructor(
    private val financeRepository: FinanceRepository
) {
    suspend operator fun invoke(masjidId: String, financeId: String): AppResult<Unit> {
        return financeRepository.deleteFinance(masjidId, financeId)
    }
}
