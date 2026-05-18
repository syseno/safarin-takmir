package com.masjid.jemaah.feature.kas

import com.masjid.core.domain.Finance
import com.masjid.core.domain.PublicFinanceSummary
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class KasIntent : BaseIntent {
    data class LoadKas(val masjidId: String) : KasIntent()
    data class LoadMore(val masjidId: String) : KasIntent()
}

sealed class KasState : BaseState {
    object Loading : KasState()
    data class Success(
        val transactions: List<Finance>,
        val summary: PublicFinanceSummary,    // totalIncome, totalExpense, balance from BE
        val currentPage: Int = 1,
        val hasMore: Boolean = false
    ) : KasState()
    data class Error(val message: String) : KasState()
}
