package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.LoginRequest
import com.masjid.core.domain.User

/**
 * Auth repository interface — DIP enforced.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<User>
    suspend fun getProfile(): AppResult<User>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun getMasjidId(): String?
}
