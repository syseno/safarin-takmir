package com.masjid.jemaah.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Masjid
import com.masjid.jemaah.data.repository.MasjidRepository
import javax.inject.Inject

class GetNearestMasjidsUseCase @Inject constructor(
    private val masjidRepository: MasjidRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        radius: Double? = null,
        limit: Int? = null,
        cityId: String? = null
    ): AppResult<List<Masjid>> {
        return masjidRepository.getNearestMasjids(latitude, longitude, radius, limit, cityId)
    }
}
