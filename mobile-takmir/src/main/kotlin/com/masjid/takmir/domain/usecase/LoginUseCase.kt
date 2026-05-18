package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.User
import com.masjid.takmir.data.repository.AuthRepository
import javax.inject.Inject

/**
 * Login use case for Takmir app.
 * Delegates to AuthRepository which enforces MASJID_ADMIN role guard.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AppResult<User> {
        if (email.isBlank()) return AppResult.Error("Email tidak boleh kosong")
        if (password.isBlank()) return AppResult.Error("Password tidak boleh kosong")
        return authRepository.login(email, password)
    }
}
