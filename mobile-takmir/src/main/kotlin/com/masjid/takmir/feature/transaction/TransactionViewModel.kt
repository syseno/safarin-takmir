package com.masjid.takmir.feature.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.takmir.core.RefreshManager
import com.masjid.takmir.core.RefreshType
import com.masjid.takmir.domain.usecase.DeleteFinanceUseCase
import com.masjid.takmir.domain.usecase.GetFinancesUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getFinancesUseCase: GetFinancesUseCase,
    private val deleteFinanceUseCase: DeleteFinanceUseCase,
    private val tokenManager: EncryptedTokenManager,
    private val refreshManager: RefreshManager
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionState())
    val state: StateFlow<TransactionState> = _state.asStateFlow()

    init {
        observeRefresh()
        fetchTransactions()
    }

    private fun observeRefresh() {
        viewModelScope.launch {
            refreshManager.refreshEvent.collect { type ->
                if (type == RefreshType.FINANCE) {
                    fetchTransactions()
                }
            }
        }
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
            _state.update { it.copy(isLoading = true, error = null) }
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.update { it.copy(isLoading = false, error = "Masjid ID tidak ditemukan") }
                return@launch
            }
            when (val result = getFinancesUseCase(masjidId)) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false, transactions = result.data) }
                }
                is AppResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    private fun deleteTransaction(financeId: String) {
        viewModelScope.launch {
            val masjidId = tokenManager.getMasjidId() ?: return@launch
            when (val result = deleteFinanceUseCase(masjidId, financeId)) {
                is AppResult.Success -> {
                    fetchTransactions()
                    refreshManager.triggerRefresh(RefreshType.FINANCE) // Notify dashboard etc
                }
                is AppResult.Error -> {
                    _state.update { it.copy(error = result.message) }
                }
            }
        }
    }
}
