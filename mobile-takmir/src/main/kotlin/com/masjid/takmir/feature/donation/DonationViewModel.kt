package com.masjid.takmir.feature.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.DonationSummary
import com.masjid.takmir.domain.usecase.GetDonationsUseCase
import com.masjid.takmir.domain.usecase.GetDonationSummaryUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationViewModel @Inject constructor(
    private val getDonationsUseCase: GetDonationsUseCase,
    private val getDonationSummaryUseCase: GetDonationSummaryUseCase,
    private val tokenManager: EncryptedTokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<DonationState>(DonationState.Loading)
    val state: StateFlow<DonationState> = _state.asStateFlow()

    init { handleIntent(DonationIntent.LoadDonations) }

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
            val donationsDeferred = async { getDonationsUseCase(masjidId) }
            val summaryDeferred = async { getDonationSummaryUseCase(masjidId) }

            val donationsResult = donationsDeferred.await()
            val summaryResult = summaryDeferred.await()

            if (donationsResult is AppResult.Error) {
                _state.value = DonationState.Error(donationsResult.message)
                return@launch
            }
            _state.value = DonationState.Success(
                donations = (donationsResult as AppResult.Success).data,
                summary = (summaryResult as? AppResult.Success)?.data ?: DonationSummary()
            )
        }
    }
}
