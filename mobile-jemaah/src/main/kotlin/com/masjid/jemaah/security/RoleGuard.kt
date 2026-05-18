package com.masjid.jemaah.security

/**
 * Role guard for Jemaah app.
 * Allows both USER and MASJID_ADMIN.
 * If MASJID_ADMIN, we can flag them as read-only.
 */
object RoleGuard {

    private val ALLOWED_ROLES = listOf("USER", "MASJID_ADMIN", "SUPER_ADMIN")

    fun isAllowed(role: String?): Boolean {
        return role in ALLOWED_ROLES
    }

    fun isReadOnlyAdmin(role: String?): Boolean {
        return role == "MASJID_ADMIN" || role == "SUPER_ADMIN"
    }
}
