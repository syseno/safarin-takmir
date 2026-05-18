package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateInventoryRequest
import com.masjid.core.domain.InventoryItem
import com.masjid.core.domain.UpdateConditionRequest
import com.masjid.core.domain.UpdateQuantityRequest

interface InventoryRepository {
    suspend fun getInventoryList(masjidId: String): AppResult<List<InventoryItem>>
    suspend fun getInventoryDetail(masjidId: String, itemId: String): AppResult<InventoryItem>
    suspend fun createInventory(masjidId: String, request: CreateInventoryRequest): AppResult<InventoryItem>
    suspend fun updateQuantity(masjidId: String, itemId: String, request: UpdateQuantityRequest): AppResult<InventoryItem>
    suspend fun updateCondition(masjidId: String, itemId: String, request: UpdateConditionRequest): AppResult<InventoryItem>
}
