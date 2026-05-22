package com.masjid.takmir.feature.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.takmir.core.RefreshManager
import com.masjid.takmir.core.RefreshType
import com.masjid.takmir.domain.usecase.CreateInventoryUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryFormViewModel @Inject constructor(
    private val createInventoryUseCase: CreateInventoryUseCase,
    private val tokenManager: EncryptedTokenManager,
    private val refreshManager: RefreshManager
) : ViewModel() {

    private val _formState = MutableStateFlow<InventoryFormState>(InventoryFormState.Idle)
    val formState: StateFlow<InventoryFormState> = _formState.asStateFlow()

    fun submit(name: String, quantity: Int, condition: String) {
        viewModelScope.launch {
            val masjidId = tokenManager.getMasjidId() ?: run {
                _formState.value = InventoryFormState.Error("Masjid ID tidak ditemukan")
                return@launch
            }
            _formState.value = InventoryFormState.Submitting
            when (val result = createInventoryUseCase(masjidId, name, quantity, condition)) {
                is AppResult.Success -> {
                    refreshManager.triggerRefresh(RefreshType.INVENTORY)
                    _formState.value = InventoryFormState.Success
                }
                is AppResult.Error -> _formState.value = InventoryFormState.Error(result.message)
            }
        }
    }
}
