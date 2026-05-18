package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Finance
import com.masjid.core.domain.UpdateFinanceRequest
import com.masjid.takmir.data.repository.FinanceRepository
import javax.inject.Inject

class UpdateFinanceUseCase @Inject constructor(
    private val financeRepository: FinanceRepository
) {
    suspend operator fun invoke(
        masjidId: String,
        financeId: String,
        request: UpdateFinanceRequest
    ): AppResult<Finance> {
        return financeRepository.updateFinance(masjidId, financeId, request)
    }
}
