package com.masjid.jemaah.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Masjid
import com.masjid.jemaah.data.repository.MasjidRepository
import javax.inject.Inject

class GetMasjidDetailUseCase @Inject constructor(
    private val masjidRepository: MasjidRepository
) {
    suspend operator fun invoke(id: String): AppResult<Masjid> {
        return masjidRepository.getMasjidDetail(id)
    }
}
