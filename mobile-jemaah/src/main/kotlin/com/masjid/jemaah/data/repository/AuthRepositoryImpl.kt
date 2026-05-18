package com.masjid.jemaah.data.repository

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.LoginRequest
import com.masjid.core.domain.User
import com.masjid.core.network.AuthApiClient
import com.masjid.jemaah.security.EncryptedTokenManager
import com.masjid.jemaah.security.RoleGuard
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiClient: AuthApiClient,
    private val tokenManager: EncryptedTokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<User> {
        return AppResult.runCatching {
            val response = authApiClient.login(LoginRequest(email, password))

            if (RoleGuard.isAllowed(response.user.role)) {
                tokenManager.saveToken(response.accessToken)
                tokenManager.saveUserRole(response.user.role)
                response.user
            } else {
                throw Exception("Akses ditolak. Anda tidak memiliki izin untuk menggunakan aplikasi ini.")
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

    override suspend fun isReadOnlyAdmin(): Boolean {
        val role = tokenManager.getUserRole()
        return RoleGuard.isReadOnlyAdmin(role)
    }
}
