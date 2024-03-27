package com.yonasoft.jadedictionary.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreSettings(private val context: Context) {

    companion object {
        val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    suspend fun setSystemTheme(useSystem: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_SYSTEM_THEME] = useSystem
        }
    }

    val isSystemTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            return@map preferences[USE_SYSTEM_THEME] ?: false
        }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDarkMode
        }
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            return@map preferences[IS_DARK_MODE] ?: false
        }
}