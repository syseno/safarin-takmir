package com.masjid.takmir.feature.dashboard

import com.masjid.core.domain.DonationSummary
import com.masjid.core.domain.Finance
import com.masjid.core.domain.MasjidEvent
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class DashboardIntent : BaseIntent {
    object LoadDashboard : DashboardIntent()
    object Refresh : DashboardIntent()
}

sealed class DashboardState : BaseState {
    object Loading : DashboardState()
    data class Success(
        val totalSaldo: Long,
        val totalIncome: Long,
        val totalExpense: Long,
        val recentTransactions: List<Finance>,
        val upcomingEvents: List<MasjidEvent>,
        val donationSummary: DonationSummary = DonationSummary(),
        val inventoryTotal: Int = 0,
        val masjidName: String = ""
    ) : DashboardState()
    data class Error(val message: String) : DashboardState()
}
