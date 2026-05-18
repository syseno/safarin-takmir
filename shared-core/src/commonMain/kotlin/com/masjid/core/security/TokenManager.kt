package com.masjid.core.security

import kotlinx.coroutines.flow.Flow

interface TokenManager {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    fun observeToken(): Flow<String?>
}
