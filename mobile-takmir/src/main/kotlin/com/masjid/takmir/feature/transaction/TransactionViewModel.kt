package com.masjid.takmir.feature.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.takmir.domain.usecase.DeleteFinanceUseCase
import com.masjid.takmir.domain.usecase.GetFinancesUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getFinancesUseCase: GetFinancesUseCase,
    private val deleteFinanceUseCase: DeleteFinanceUseCase,
    private val tokenManager: EncryptedTokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<TransactionState>(TransactionState.Loading)
    val state: StateFlow<TransactionState> = _state.asStateFlow()

    init {
        fetchTransactions()
    }

    fun handleIntent(intent: TransactionIntent) {
        when (intent) {
            is TransactionIntent.LoadTransactions -> fetchTransactions()
            is TransactionIntent.DeleteTransaction -> deleteTransaction(intent.financeId)
            is TransactionIntent.Refresh -> fetchTransactions()
        }
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            _state.value = TransactionState.Loading
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.value = TransactionState.Error("Masjid ID tidak ditemukan")
                return@launch
            }
            when (val result = getFinancesUseCase(masjidId)) {
                is AppResult.Success -> {
                    _state.value = TransactionState.Success(result.data)
                }
                is AppResult.Error -> {
                    _state.value = TransactionState.Error(result.message)
                }
            }
        }
    }

    private fun deleteTransaction(financeId: String) {
        viewModelScope.launch {
            val masjidId = tokenManager.getMasjidId() ?: return@launch
            // Optimistic update could be implemented here
            when (val result = deleteFinanceUseCase(masjidId, financeId)) {
                is AppResult.Success -> {
                    fetchTransactions() // Refresh list
                }
                is AppResult.Error -> {
                    // Handle error, maybe show a snackbar (requires effect channel)
                    // For now, just refresh to ensure state is consistent
                    fetchTransactions()
                }
            }
        }
    }
}
