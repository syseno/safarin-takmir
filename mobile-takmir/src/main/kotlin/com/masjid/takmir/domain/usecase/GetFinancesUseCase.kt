package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Finance
import com.masjid.takmir.data.repository.FinanceRepository
import javax.inject.Inject

class GetFinancesUseCase @Inject constructor(
    private val financeRepository: FinanceRepository
) {
    suspend operator fun invoke(masjidId: String): AppResult<List<Finance>> {
        return financeRepository.getFinances(masjidId)
    }
}
