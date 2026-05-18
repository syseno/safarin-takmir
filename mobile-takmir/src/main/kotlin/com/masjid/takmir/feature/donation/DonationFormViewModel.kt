package com.masjid.takmir.feature.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.takmir.domain.usecase.CreateDonationUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DonationFormState {
    object Idle : DonationFormState()
    object Submitting : DonationFormState()
    object Success : DonationFormState()
    data class Error(val message: String) : DonationFormState()
}

@HiltViewModel
class DonationFormViewModel @Inject constructor(
    private val createDonationUseCase: CreateDonationUseCase,
    private val tokenManager: EncryptedTokenManager
) : ViewModel() {

    private val _formState = MutableStateFlow<DonationFormState>(DonationFormState.Idle)
    val formState: StateFlow<DonationFormState> = _formState.asStateFlow()

    fun submit(type: String, amount: Double, description: String) {
        viewModelScope.launch {
            val masjidId = tokenManager.getMasjidId() ?: run {
                _formState.value = DonationFormState.Error("Masjid ID tidak ditemukan")
                return@launch
            }
            _formState.value = DonationFormState.Submitting
            when (val result = createDonationUseCase(masjidId, type, amount, description)) {
                is AppResult.Success -> _formState.value = DonationFormState.Success
                is AppResult.Error -> _formState.value = DonationFormState.Error(result.message)
            }
        }
    }
}
