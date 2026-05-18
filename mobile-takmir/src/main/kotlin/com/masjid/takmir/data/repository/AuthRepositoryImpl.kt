package com.masjid.takmir.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.LoginRequest
import com.masjid.core.domain.User
import com.masjid.core.network.AuthApiClient
import com.masjid.takmir.security.EncryptedTokenManager
import com.masjid.takmir.security.RoleGuard
import com.masjid.takmir.security.RoleValidationResult
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiClient: AuthApiClient,
    private val tokenManager: EncryptedTokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<User> {
        return AppResult.runCatching {
            val response = authApiClient.login(LoginRequest(email, password))

            // Enforce role guard — MASJID_ADMIN only for Takmir app
            when (val validation = RoleGuard.validate(response.user.role)) {
                is RoleValidationResult.Allowed -> {
                    // response.token matches BE field name
                    tokenManager.saveToken(response.token)
                    tokenManager.saveUserRole(response.user.role)
                    // masjidId comes from the user object in login response
                    response.user.masjidId?.let { tokenManager.saveMasjidId(it) }
                    response.user
                }
                is RoleValidationResult.Denied -> {
                    throw Exception(validation.message)
                }
            }
        }
    }

    override suspend fun getProfile(): AppResult<User> {
        return AppResult.runCatching {
            authApiClient.getProfile()
        }
    }

    override suspend fun logout() {
        tokenManager.clearAll()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }

    override suspend fun getMasjidId(): String? {
        return tokenManager.getMasjidId()
    }
}
