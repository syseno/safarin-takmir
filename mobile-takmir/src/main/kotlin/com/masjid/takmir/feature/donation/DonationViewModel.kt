package com.masjid.takmir.feature.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.takmir.core.RefreshManager
import com.masjid.takmir.core.RefreshType
import com.masjid.takmir.domain.usecase.GetDonationSummaryUseCase
import com.masjid.takmir.domain.usecase.GetDonationsUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationViewModel @Inject constructor(
    private val getDonationsUseCase: GetDonationsUseCase,
    private val getDonationSummaryUseCase: GetDonationSummaryUseCase,
    private val tokenManager: EncryptedTokenManager,
    private val refreshManager: RefreshManager
) : ViewModel() {

    private val _state = MutableStateFlow<DonationState>(DonationState.Loading)
    val state: StateFlow<DonationState> = _state.asStateFlow()

    init {
        observeRefresh()
        fetchDonations()
    }

    private fun observeRefresh() {
        viewModelScope.launch {
            refreshManager.refreshEvent.collect { type ->
                if (type == RefreshType.DONATION) {
                    fetchDonations()
                }
            }
        }
    }

    fun handleIntent(intent: DonationIntent) {
        when (intent) {
            is DonationIntent.LoadDonations -> fetchDonations()
            is DonationIntent.Refresh -> fetchDonations()
        }
    }

    private fun fetchDonations() {
        viewModelScope.launch {
            _state.value = DonationState.Loading
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.value = DonationState.Error("Masjid ID tidak ditemukan")
                return@launch
            }
            
            val donationsResult = getDonationsUseCase(masjidId)
            val summaryResult = getDonationSummaryUseCase(masjidId)

            if (donationsResult is AppResult.Success && summaryResult is AppResult.Success) {
                _state.value = DonationState.Success(donationsResult.data, summaryResult.data)
            } else {
                val msg = (donationsResult as? AppResult.Error)?.message ?: (summaryResult as? AppResult.Error)?.message ?: "Unknown error"
                _state.value = DonationState.Error(msg)
            }
        }
    }
}
