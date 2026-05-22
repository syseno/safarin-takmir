package com.masjid.takmir.feature.transaction

import com.masjid.core.domain.Finance
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class TransactionIntent : BaseIntent {
    data class LoadTransactions(val masjidId: String) : TransactionIntent()
    data class DeleteTransaction(val financeId: String) : TransactionIntent()
    object Refresh : TransactionIntent()
}

data class TransactionState(
    val transactions: List<Finance> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) : BaseState
