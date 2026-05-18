package com.masjid.takmir.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.masjid.core.security.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Secure JWT token storage using EncryptedSharedPreferences.
 * No sensitive data is ever logged.
 */
class EncryptedTokenManager(context: Context) : TokenManager {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs = EncryptedSharedPreferences.create(
        "takmir_secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _tokenFlow = MutableStateFlow<String?>(prefs.getString(KEY_JWT, null))

    override suspend fun saveToken(token: String) {
        prefs.edit().putString(KEY_JWT, token).apply()
        _tokenFlow.value = token
    }

    override suspend fun getToken(): String? {
        return prefs.getString(KEY_JWT, null)
    }

    override suspend fun clearToken() {
        prefs.edit().remove(KEY_JWT).apply()
        _tokenFlow.value = null
    }

    override fun observeToken(): Flow<String?> = _tokenFlow.asStateFlow()

    /**
     * Save user role alongside token for role guard checks.
     */
    fun saveUserRole(role: String) {
        prefs.edit().putString(KEY_ROLE, role).apply()
    }

    fun getUserRole(): String? {
        return prefs.getString(KEY_ROLE, null)
    }

    fun saveMasjidId(masjidId: String) {
        prefs.edit().putString(KEY_MASJID_ID, masjidId).apply()
    }

    fun getMasjidId(): String? {
        return prefs.getString(KEY_MASJID_ID, null)
    }

    fun clearAll() {
        prefs.edit().clear().apply()
        _tokenFlow.value = null
    }

    companion object {
        private const val KEY_JWT = "jwt_token"
        private const val KEY_ROLE = "user_role"
        private const val KEY_MASJID_ID = "masjid_id"
    }
}
