package com.masjid.jemaah.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val PRAYER_METHOD_KEY = intPreferencesKey("prayer_method")
        private val THEME_MODE_KEY = intPreferencesKey("theme_mode")
    }

    val prayerMethod: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PRAYER_METHOD_KEY] ?: 20 // Default to Kemenag Indonesia
    }

    val themeMode: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE_KEY] ?: 0 // Default to System (0)
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
