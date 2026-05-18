package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateDonationRequest
import com.masjid.core.domain.Donation
import com.masjid.core.domain.DonationSummary
import com.masjid.core.network.TakmirApiClient
import com.masjid.takmir.data.local.DonationDao
import com.masjid.takmir.data.local.entity.DonationEntity
import javax.inject.Inject

class DonationRepositoryImpl @Inject constructor(
    private val apiClient: TakmirApiClient,
    private val donationDao: DonationDao
) : DonationRepository {

    override suspend fun getDonations(masjidId: String): AppResult<List<Donation>> {
        return AppResult.runCatching {
            val donations = apiClient.getDonations(masjidId)
            donationDao.deleteAllByMasjidId(masjidId)
            donationDao.insertAll(donations.map { it.toEntity(masjidId) })
            donations
        }
    }

    override suspend fun createDonation(
        masjidId: String,
        request: CreateDonationRequest
    ): AppResult<Donation> {
        return AppResult.runCatching {
            val donation = apiClient.createDonation(masjidId, request)
            donationDao.insert(donation.toEntity(masjidId))
            donation
        }
    }

    override suspend fun getDonationSummary(masjidId: String): AppResult<DonationSummary> {
        return AppResult.runCatching {
            apiClient.getDonationSummary(masjidId)
        }
    }

    private fun Donation.toEntity(masjidId: String) = DonationEntity(
        id = id,
        type = type,
        amount = amount,
        description = description,
        createdAt = createdAt,
        masjidId = masjidId
    )
}
