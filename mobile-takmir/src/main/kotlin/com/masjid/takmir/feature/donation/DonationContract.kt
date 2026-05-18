package com.masjid.takmir.feature.donation

import com.masjid.core.domain.Donation
import com.masjid.core.domain.DonationSummary
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class DonationIntent : BaseIntent {
    object LoadDonations : DonationIntent()
    object Refresh : DonationIntent()
}

sealed class DonationState : BaseState {
    object Loading : DonationState()
    data class Success(
        val donations: List<Donation>,
        val summary: DonationSummary
    ) : DonationState()
    data class Error(val message: String) : DonationState()
}
