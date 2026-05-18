package com.masjid.jemaah.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.PublicFinanceResponse
import com.masjid.jemaah.data.repository.FinanceRepository
import javax.inject.Inject

class GetKasUseCase @Inject constructor(
    private val financeRepository: FinanceRepository
) {
    suspend operator fun invoke(
        masjidId: String,
        page: Int = 1,
        limit: Int = 20
    ): AppResult<PublicFinanceResponse> {
        return financeRepository.getFinances(masjidId, page, limit)
    }
}
