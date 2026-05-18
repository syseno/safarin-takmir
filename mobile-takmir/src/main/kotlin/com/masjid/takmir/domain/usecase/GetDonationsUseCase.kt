package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Donation
import com.masjid.takmir.data.repository.DonationRepository
import javax.inject.Inject

class GetDonationsUseCase @Inject constructor(
    private val donationRepository: DonationRepository
) {
    suspend operator fun invoke(masjidId: String): AppResult<List<Donation>> =
        donationRepository.getDonations(masjidId)
}
