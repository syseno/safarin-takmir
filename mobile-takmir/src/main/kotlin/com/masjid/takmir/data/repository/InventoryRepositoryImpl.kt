package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateInventoryRequest
import com.masjid.core.domain.InventoryItem
import com.masjid.core.domain.UpdateConditionRequest
import com.masjid.core.domain.UpdateQuantityRequest
import com.masjid.core.network.TakmirApiClient
import com.masjid.takmir.data.local.InventoryDao
import com.masjid.takmir.data.local.entity.InventoryEntity
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val apiClient: TakmirApiClient,
    private val inventoryDao: InventoryDao
) : InventoryRepository {

    override suspend fun getInventoryList(masjidId: String): AppResult<List<InventoryItem>> {
        return AppResult.runCatching {
            val items = apiClient.getInventoryList(masjidId)
            inventoryDao.deleteAllByMasjidId(masjidId)
            inventoryDao.insertAll(items.map { it.toEntity(masjidId) })
            items
        }
    }

    override suspend fun getInventoryDetail(masjidId: String, itemId: String): AppResult<InventoryItem> {
        return AppResult.runCatching {
            apiClient.getInventoryDetail(masjidId, itemId)
        }
    }

    override suspend fun createInventory(
        masjidId: String,
        request: CreateInventoryRequest
    ): AppResult<InventoryItem> {
        return AppResult.runCatching {
            val item = apiClient.createInventory(masjidId, request)
            inventoryDao.insert(item.toEntity(masjidId))
            item
        }
    }

    override suspend fun updateQuantity(
        masjidId: String,
        itemId: String,
        request: UpdateQuantityRequest
    ): AppResult<InventoryItem> {
        return AppResult.runCatching {
            val item = apiClient.updateInventoryQuantity(masjidId, itemId, request)
            inventoryDao.insert(item.toEntity(masjidId))
            item
        }
    }

    override suspend fun updateCondition(
        masjidId: String,
        itemId: String,
        request: UpdateConditionRequest
    ): AppResult<InventoryItem> {
        return AppResult.runCatching {
            val item = apiClient.updateInventoryCondition(masjidId, itemId, request)
            inventoryDao.insert(item.toEntity(masjidId))
            item
        }
    }

    private fun InventoryItem.toEntity(masjidId: String) = InventoryEntity(
        id = id,
        name = name,
        quantity = quantity,
        condition = condition,
        createdAt = createdAt,
        masjidId = masjidId
    )
}
