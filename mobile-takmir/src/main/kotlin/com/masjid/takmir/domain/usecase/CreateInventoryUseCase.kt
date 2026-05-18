package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateInventoryRequest
import com.masjid.core.domain.InventoryItem
import com.masjid.takmir.data.repository.InventoryRepository
import javax.inject.Inject

class CreateInventoryUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(
        masjidId: String,
        name: String,
        quantity: Int,
        condition: String
    ): AppResult<InventoryItem> {
        if (name.isBlank()) return AppResult.Error("Nama barang tidak boleh kosong")
        if (quantity < 0) return AppResult.Error("Jumlah tidak boleh negatif")
        return inventoryRepository.createInventory(
            masjidId,
            CreateInventoryRequest(name = name, quantity = quantity, condition = condition)
        )
    }
}
