package com.masjid.takmir.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.takmir.domain.usecase.GetDashboardUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase,
    private val tokenManager: EncryptedTokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        handleIntent(DashboardIntent.LoadDashboard)
    }

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadDashboard -> fetchDashboard()
            is DashboardIntent.Refresh -> fetchDashboard()
        }
    }

    private fun fetchDashboard() {
        viewModelScope.launch {
            _state.value = DashboardState.Loading
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.value = DashboardState.Error("Masjid ID tidak ditemukan")
                return@launch
            }
            when (val result = getDashboardUseCase(masjidId)) {
                is AppResult.Success -> {
                    val data = result.data
                    // Map BE response shape to UI state
                    _state.value = DashboardState.Success(
                        totalSaldo = data.finance.balance.toLong(),
                        totalIncome = data.finance.totalIncome.toLong(),
                        totalExpense = data.finance.totalExpense.toLong(),
                        recentTransactions = data.recentFinance,
                        upcomingEvents = data.upcomingEvents,
                        donationSummary = data.donations,
                        inventoryTotal = data.inventory.totalItems
                    )
                }
                is AppResult.Error -> {
                    _state.value = DashboardState.Error(result.message)
                }
            }
        }
    }
}
