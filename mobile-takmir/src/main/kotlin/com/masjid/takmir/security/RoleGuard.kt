package com.masjid.takmir.security

/**
 * Role guard for Takmir app.
 * STRICT: Only MASJID_ADMIN role is allowed.
 * Any other role triggers force logout.
 */
object RoleGuard {

    private const val ALLOWED_ROLE = "MASJID_ADMIN"

    /**
     * Returns true if the given role is allowed to use the Takmir app.
     */
    fun isAllowed(role: String?): Boolean {
        return role == ALLOWED_ROLE
    }

    /**
     * Validates the role. Returns an error message if the role is not allowed.
     */
    fun validate(role: String?): RoleValidationResult {
        return if (role == ALLOWED_ROLE) {
            RoleValidationResult.Allowed
        } else {
            RoleValidationResult.Denied(
                message = "Akses ditolak. Hanya MASJID_ADMIN yang dapat menggunakan aplikasi Takmir."
            )
        }
    }
}

sealed class RoleValidationResult {
    object Allowed : RoleValidationResult()
    data class Denied(val message: String) : RoleValidationResult()
}
