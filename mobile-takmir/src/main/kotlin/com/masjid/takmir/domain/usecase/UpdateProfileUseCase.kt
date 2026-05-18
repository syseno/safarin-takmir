package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Masjid
import com.masjid.core.domain.UpdateProfileRequest
import com.masjid.takmir.data.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(masjidId: String, request: UpdateProfileRequest): AppResult<Masjid> {
        return profileRepository.updateProfile(masjidId, request)
    }
}
