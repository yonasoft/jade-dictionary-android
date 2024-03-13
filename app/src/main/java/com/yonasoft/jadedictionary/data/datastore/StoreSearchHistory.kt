package com.yonasoft.jadedictionary.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("searchHistory")

class StoreSearchHistory(private val context: Context) {
    private val gson = Gson()

    companion object {
        val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
    }

    // Storing the list of search history
    suspend fun storeSearchHistory(list: List<String>) {
        val jsonString = gson.toJson(list)
        context.dataStore.edit { preferences ->
            preferences[SEARCH_HISTORY_KEY] = jsonString
        }
    }

    // Retrieving the list of search history
    val searchHistory: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[SEARCH_HISTORY_KEY] ?: ""
            return@map if (jsonString.isNotEmpty()) {
                gson.fromJson(jsonString, object : TypeToken<List<String>>() {}.type)
            } else {
                emptyList<String>()
            }
        }

    // Helper function to get search history synchronously (use cautiously)
    fun getSearchHistorySync(): List<String> = runBlocking { searchHistory.first() }
}
