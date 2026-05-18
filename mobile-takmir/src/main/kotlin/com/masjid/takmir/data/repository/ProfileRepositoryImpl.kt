package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Masjid
import com.masjid.core.domain.UpdateProfileRequest
import com.masjid.core.network.TakmirApiClient
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiClient: TakmirApiClient
) : ProfileRepository {

    override suspend fun getProfile(masjidId: String): AppResult<Masjid> {
        return AppResult.runCatching {
            apiClient.getProfile(masjidId)
        }
    }

    override suspend fun updateProfile(
        masjidId: String,
        request: UpdateProfileRequest
    ): AppResult<Masjid> {
        return AppResult.runCatching {
            apiClient.updateProfile(masjidId, request)
        }
    }
}
