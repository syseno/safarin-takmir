package com.masjid.takmir.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "takmir_settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val PRAYER_METHOD_KEY = intPreferencesKey("prayer_method")
        private val THEME_MODE_KEY = intPreferencesKey("theme_mode")
    }

    val prayerMethod: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PRAYER_METHOD_KEY] ?: 20 // Default to Kemenag Indonesia (20)
    }

    val themeMode: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE_KEY] ?: 0 // 0: System, 1: Light, 2: Dark
    }

    suspend fun setPrayerMethod(methodId: Int) {
        context.dataStore.edit { preferences ->
            preferences[PRAYER_METHOD_KEY] = methodId
        }
    }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }
}
