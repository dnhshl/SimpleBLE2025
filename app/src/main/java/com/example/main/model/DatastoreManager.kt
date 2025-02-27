package com.example.main.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

// Repository für den Zugriff auf den DataStore;
// ----------------------------------------------------------------

class DatastoreManager(private val dataStore: DataStore<Preferences>) {
    companion object {
        val PERSISTANT_STATE_KEY = stringPreferencesKey("p_state")
    }

    fun getPersistantState(): Flow<PersistantUiState> =
        dataStore.data.map { preferences ->
            preferences[PERSISTANT_STATE_KEY]?.let { jsonString ->
                try {
                    Json.decodeFromString(PersistantUiState.serializer(), jsonString)
                } catch (e: Exception) {
                    PersistantUiState() // Fallback if parsing fails
                }
            } ?: PersistantUiState() // Default if no value stored
        }

    suspend fun savePersitantState(state: PersistantUiState) {
        dataStore.edit { preferences ->
            preferences[PERSISTANT_STATE_KEY] =
                Json.encodeToString(PersistantUiState.serializer(), state)
        }
    }
}