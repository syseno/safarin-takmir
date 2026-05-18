package com.masjid.takmir.feature.transaction

import com.masjid.core.domain.Finance
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class TransactionIntent : BaseIntent {
    data class LoadTransactions(val masjidId: String) : TransactionIntent()
    data class DeleteTransaction(val financeId: String) : TransactionIntent()
    object Refresh : TransactionIntent()
}

sealed class TransactionState : BaseState {
    object Loading : TransactionState()
    data class Success(val transactions: List<Finance>) : TransactionState()
    data class Error(val message: String) : TransactionState()
}
