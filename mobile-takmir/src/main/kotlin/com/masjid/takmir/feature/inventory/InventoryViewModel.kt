package com.masjid.takmir.feature.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.UpdateConditionRequest
import com.masjid.core.domain.UpdateQuantityRequest
import com.masjid.takmir.core.RefreshManager
import com.masjid.takmir.core.RefreshType
import com.masjid.takmir.domain.usecase.GetInventoryListUseCase
import com.masjid.takmir.data.repository.InventoryRepository
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryListUseCase: GetInventoryListUseCase,
    private val inventoryRepository: InventoryRepository,
    private val tokenManager: EncryptedTokenManager,
    private val refreshManager: RefreshManager
) : ViewModel() {

    private val _state = MutableStateFlow<InventoryState>(InventoryState.Loading)
    val state: StateFlow<InventoryState> = _state.asStateFlow()

    init {
        observeRefresh()
        fetchInventory()
    }

    private fun observeRefresh() {
        viewModelScope.launch {
            refreshManager.refreshEvent.collect { type ->
                if (type == RefreshType.INVENTORY) {
                    fetchInventory()
                }
            }
        }
    }

    fun handleIntent(intent: InventoryIntent) {
        when (intent) {
            is InventoryIntent.LoadInventory -> fetchInventory()
            is InventoryIntent.Refresh -> fetchInventory()
            is InventoryIntent.UpdateQuantity -> updateQuantity(intent.itemId, intent.quantity)
            is InventoryIntent.UpdateCondition -> updateCondition(intent.itemId, intent.condition)
        }
    }

    private fun fetchInventory() {
        viewModelScope.launch {
            _state.value = InventoryState.Loading
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.value = InventoryState.Error("Masjid ID tidak ditemukan")
                return@launch
            }
            when (val result = getInventoryListUseCase(masjidId)) {
                is AppResult.Success -> _state.value = InventoryState.Success(result.data)
                is AppResult.Error -> _state.value = InventoryState.Error(result.message)
            }
        }
    }

    private fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            val masjidId = tokenManager.getMasjidId() ?: return@launch
            inventoryRepository.updateQuantity(masjidId, itemId, UpdateQuantityRequest(quantity))
            fetchInventory()
            refreshManager.triggerRefresh(RefreshType.INVENTORY) // Notify others
        }
    }

    private fun updateCondition(itemId: String, condition: String) {
        viewModelScope.launch {
            val masjidId = tokenManager.getMasjidId() ?: return@launch
            inventoryRepository.updateCondition(masjidId, itemId, UpdateConditionRequest(condition))
            fetchInventory()
            refreshManager.triggerRefresh(RefreshType.INVENTORY) // Notify others
        }
    }
}
