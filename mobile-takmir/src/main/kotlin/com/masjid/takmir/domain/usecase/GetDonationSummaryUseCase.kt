package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.DonationSummary
import com.masjid.takmir.data.repository.DonationRepository
import javax.inject.Inject

class GetDonationSummaryUseCase @Inject constructor(
    private val donationRepository: DonationRepository
) {
    suspend operator fun invoke(masjidId: String): AppResult<DonationSummary> =
        donationRepository.getDonationSummary(masjidId)
}
