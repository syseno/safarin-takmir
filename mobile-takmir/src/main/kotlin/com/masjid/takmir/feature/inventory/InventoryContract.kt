package com.masjid.takmir.feature.inventory

import com.masjid.core.domain.InventoryItem
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class InventoryIntent : BaseIntent {
    object LoadInventory : InventoryIntent()
    data class UpdateQuantity(val itemId: String, val quantity: Int) : InventoryIntent()
    data class UpdateCondition(val itemId: String, val condition: String) : InventoryIntent()
    object Refresh : InventoryIntent()
}

sealed class InventoryState : BaseState {
    object Loading : InventoryState()
    data class Success(val items: List<InventoryItem>) : InventoryState()
    data class Error(val message: String) : InventoryState()
}
