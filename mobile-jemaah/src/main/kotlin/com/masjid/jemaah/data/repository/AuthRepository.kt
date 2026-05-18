package com.masjid.jemaah.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.User

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<User>
    suspend fun getProfile(): AppResult<User>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun isReadOnlyAdmin(): Boolean
}
