package com.masjid.takmir.feature.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateFinanceRequest
import com.masjid.core.domain.Finance
import com.masjid.core.domain.UpdateFinanceRequest
import com.masjid.core.mvi.BaseState
import com.masjid.takmir.data.repository.FinanceRepository
import com.masjid.takmir.domain.usecase.CreateFinanceUseCase
import com.masjid.takmir.domain.usecase.UpdateFinanceUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TransactionFormState : BaseState {
    object Idle : TransactionFormState()
    object Loading : TransactionFormState()
    data class Editing(val transaction: Finance) : TransactionFormState()
    object Saving : TransactionFormState()
    object Success : TransactionFormState()
    data class Error(val message: String) : TransactionFormState()
}

@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    private val createFinanceUseCase: CreateFinanceUseCase,
    private val updateFinanceUseCase: UpdateFinanceUseCase,
    private val financeRepository: FinanceRepository,
    private val tokenManager: EncryptedTokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<TransactionFormState>(TransactionFormState.Idle)
    val state: StateFlow<TransactionFormState> = _state.asStateFlow()

    fun initForm(financeId: String?) {
        if (financeId == null) {
            _state.value = TransactionFormState.Idle
            return
        }

        viewModelScope.launch {
            _state.value = TransactionFormState.Loading
            val masjidId = tokenManager.getMasjidId() ?: return@launch
            
            // To properly edit, we'd normally get it from DB or API.
            // Assuming getting the list and finding it is quick.
            when (val result = financeRepository.getFinances(masjidId)) {
                is AppResult.Success -> {
                    val tx = result.data.find { it.id == financeId }
                    if (tx != null) {
                        _state.value = TransactionFormState.Editing(tx)
                    } else {
                        _state.value = TransactionFormState.Error("Transaksi tidak ditemukan")
                    }
                }
                is AppResult.Error -> {
                    _state.value = TransactionFormState.Error(result.message)
                }
            }
        }
    }

    fun saveTransaction(financeId: String?, title: String, amount: Long, type: String, description: String) {
        viewModelScope.launch {
            _state.value = TransactionFormState.Saving
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.value = TransactionFormState.Error("Masjid ID tidak ditemukan")
                return@launch
            }

            val desc = description.ifBlank { null }

            val result = if (financeId == null) {
                createFinanceUseCase(masjidId, CreateFinanceRequest(title, amount.toDouble(), type, description))
            } else {
                updateFinanceUseCase(masjidId, financeId, UpdateFinanceRequest(title, amount.toDouble(), type, desc))
            }

            when (result) {
                is AppResult.Success -> _state.value = TransactionFormState.Success
                is AppResult.Error -> _state.value = TransactionFormState.Error(result.message)
            }
        }
    }
}
