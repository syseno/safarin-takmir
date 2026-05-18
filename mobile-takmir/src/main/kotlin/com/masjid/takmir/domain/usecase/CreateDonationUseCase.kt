package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateDonationRequest
import com.masjid.core.domain.Donation
import com.masjid.takmir.data.repository.DonationRepository
import javax.inject.Inject

class CreateDonationUseCase @Inject constructor(
    private val donationRepository: DonationRepository
) {
    suspend operator fun invoke(
        masjidId: String,
        type: String,
        amount: Double,
        description: String
    ): AppResult<Donation> {
        if (type.isBlank()) return AppResult.Error("Jenis donasi harus dipilih")
        if (amount <= 0) return AppResult.Error("Jumlah donasi harus lebih dari 0")
        if (description.isBlank()) return AppResult.Error("Keterangan tidak boleh kosong")
        return donationRepository.createDonation(
            masjidId,
            CreateDonationRequest(type = type, amount = amount, description = description)
        )
    }
}
