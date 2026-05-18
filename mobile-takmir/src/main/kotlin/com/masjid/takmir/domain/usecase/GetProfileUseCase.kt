package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Masjid
import com.masjid.takmir.data.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(masjidId: String): AppResult<Masjid> {
        return profileRepository.getProfile(masjidId)
    }
}
