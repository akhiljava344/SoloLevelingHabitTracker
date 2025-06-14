package com.app.sololevelinghabittracker.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate // Import LocalDate

// DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

class AppPreferencesManager(private val context: Context) {

    // Keys for preferences
    private object PreferencesKeys {
        val JOURNAL_ENTRY_PREFIX = stringPreferencesKey("journal_entry_") // Dynamic key for date
        val MOOD_ENTRY_PREFIX = intPreferencesKey("mood_entry_")       // Dynamic key for date
        val FAIL_REASON_LAST_UPDATED_DATE = stringPreferencesKey("fail_reason_last_updated_date")

        // NEW Keys for global user stats
        val USER_XP = intPreferencesKey("user_xp")
        val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
    }

    // --- Journal Entry & Mood (Existing) ---
    suspend fun saveJournalEntry(date: String, entry: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(PreferencesKeys.JOURNAL_ENTRY_PREFIX.name + date)] = entry
        }
    }

    suspend fun getJournalEntry(date: String): String {
        return context.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey(PreferencesKeys.JOURNAL_ENTRY_PREFIX.name + date)] ?: ""
            }
            .firstOrNull() ?: ""
    }

    suspend fun saveMoodEntry(date: String, mood: Int) {
        context.dataStore.edit { preferences ->
            preferences[intPreferencesKey(PreferencesKeys.MOOD_ENTRY_PREFIX.name + date)] = mood
        }
    }

    suspend fun getMoodEntry(date: String): Int {
        return context.dataStore.data
            .map { preferences ->
                preferences[intPreferencesKey(PreferencesKeys.MOOD_ENTRY_PREFIX.name + date)] ?: 0
            }
            .firstOrNull() ?: 0
    }

    // --- Fail Reason Last Updated Date (Existing) ---
    suspend fun saveFailReasonLastUpdatedDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FAIL_REASON_LAST_UPDATED_DATE] = date
        }
    }

    suspend fun getFailReasonLastUpdatedDate(): String {
        return context.dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.FAIL_REASON_LAST_UPDATED_DATE] ?: ""
            }
            .firstOrNull() ?: ""
    }

    suspend fun resetFailReasons() {
        // This method can be expanded if you add specific fail reason keys to clear.
        // For now, it just demonstrates where you would clear them.
        context.dataStore.edit { preferences ->
            // Example: preferences.remove(PreferencesKeys.SOME_SPECIFIC_FAIL_REASON_KEY)
        }
    }

    // --- NEW: User XP (Managed by DataStore) ---
    suspend fun saveUserXp(xp: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_XP] = xp
        }
    }

    fun getUserXp(): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.USER_XP] ?: 0 // Default to 0 XP
            }
    }

    // --- NEW: Last Reset Date (Managed by DataStore) ---
    suspend fun saveLastResetDate(date: LocalDate) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_RESET_DATE] = date.toString() // Store as String
        }
    }

    fun getLastResetDate(): Flow<LocalDate?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.LAST_RESET_DATE]?.let {
                    LocalDate.parse(it)
                } ?: LocalDate.MIN // Default to LocalDate.MIN if not set
            }
    }
}