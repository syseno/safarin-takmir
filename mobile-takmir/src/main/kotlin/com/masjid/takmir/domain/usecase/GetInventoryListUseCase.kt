package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.InventoryItem
import com.masjid.takmir.data.repository.InventoryRepository
import javax.inject.Inject

class GetInventoryListUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(masjidId: String): AppResult<List<InventoryItem>> =
        inventoryRepository.getInventoryList(masjidId)
}
