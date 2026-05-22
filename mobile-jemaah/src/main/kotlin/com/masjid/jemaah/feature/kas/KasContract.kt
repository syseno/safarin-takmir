package com.masjid.jemaah.feature.kas

import com.masjid.core.domain.Finance
import com.masjid.core.domain.PublicFinanceSummary
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class KasIntent : BaseIntent {
    data class LoadKas(val masjidId: String) : KasIntent()
    data class LoadMore(val masjidId: String) : KasIntent()
}

data class KasState(
    val transactions: List<Finance> = emptyList(),
    val summary: PublicFinanceSummary = PublicFinanceSummary(),
    val isLoading: Boolean = false,
    val isMoreLoading: Boolean = false,
    val currentPage: Int = 1,
    val hasMore: Boolean = false,
    val error: String? = null
) : BaseState
