package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateDonationRequest
import com.masjid.core.domain.Donation
import com.masjid.core.domain.DonationSummary

interface DonationRepository {
    suspend fun getDonations(masjidId: String): AppResult<List<Donation>>
    suspend fun createDonation(masjidId: String, request: CreateDonationRequest): AppResult<Donation>
    suspend fun getDonationSummary(masjidId: String): AppResult<DonationSummary>
}
