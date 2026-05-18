package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Masjid
import com.masjid.core.domain.UpdateProfileRequest

/**
 * Profile repository interface — DIP enforced.
 */
interface ProfileRepository {
    suspend fun getProfile(masjidId: String): AppResult<Masjid>
    suspend fun updateProfile(masjidId: String, request: UpdateProfileRequest): AppResult<Masjid>
}
