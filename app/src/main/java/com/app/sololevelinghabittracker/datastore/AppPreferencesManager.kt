package com.app.sololevelinghabittracker.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import kotlinx.coroutines.flow.first

// Define the DataStore
private val Context.dataStore by preferencesDataStore(name = "app_preferences")

object AppPrefsKeys {
    val IS_FIRST_LAUNCH_DONE = booleanPreferencesKey("is_first_launch_done")
    val COMPLETED_SESSIONS = intPreferencesKey("completed_sessions_today")
    val LAST_UPDATED_DATE = stringPreferencesKey("last_updated_date")

    // TODO: Newly Added - For Fail Reason tracking
    val FAIL_REASON_LIST = stringPreferencesKey("fail_reason_list") // Stores CSV string of reasons
    val FAIL_REASON_LAST_UPDATED_DATE = stringPreferencesKey("fail_reason_last_updated_date")
    val LAST_HABIT_RESET_DATE = stringPreferencesKey("last_habit_reset_date") // ✅ NEW
}

class AppPreferencesManager(private val context: Context) {

    // ✅ Habit Seeder use
    suspend fun setIsFirstLaunchDone(done: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AppPrefsKeys.IS_FIRST_LAUNCH_DONE] = done
        }
    }

    suspend fun getIsFirstLaunchDone(): Boolean {
        return context.dataStore.data
            .map { prefs -> prefs[AppPrefsKeys.IS_FIRST_LAUNCH_DONE] ?: false }
            .first()
    }

    // ✅ Pomodoro use
    val completedSessionsToday: Flow<Int> = context.dataStore.data.map { prefs ->
        val lastUpdated = prefs[AppPrefsKeys.LAST_UPDATED_DATE]
        val today = LocalDate.now().toString()
        if (lastUpdated != today) 0
        else prefs[AppPrefsKeys.COMPLETED_SESSIONS] ?: 0
    }

    suspend fun incrementSessionCount() {
        context.dataStore.edit { prefs ->
            val today = LocalDate.now().toString()
            val lastUpdated = prefs[AppPrefsKeys.LAST_UPDATED_DATE]

            val currentCount = if (lastUpdated == today) {
                prefs[AppPrefsKeys.COMPLETED_SESSIONS] ?: 0
            } else {
                0
            }

            prefs[AppPrefsKeys.COMPLETED_SESSIONS] = currentCount + 1
            prefs[AppPrefsKeys.LAST_UPDATED_DATE] = today
        }
    }

    // TODO: ✅ Newly Added - Fail Reason Functions

    // Save fail reasons list (as comma-separated String)
    suspend fun saveFailReasons(reasons: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[AppPrefsKeys.FAIL_REASON_LIST] = reasons.joinToString(",") // Save as CSV
            prefs[AppPrefsKeys.FAIL_REASON_LAST_UPDATED_DATE] = LocalDate.now().toString()
        }
    }

    // Retrieve fail reasons list (converted back to List<String>)
    suspend fun getFailReasons(): List<String> {
        return context.dataStore.data
            .map { prefs ->
                prefs[AppPrefsKeys.FAIL_REASON_LIST]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            }
            .first()
    }

    // Get last updated date of fail reasons
    suspend fun getFailReasonLastUpdatedDate(): String {
        return context.dataStore.data
            .map { prefs -> prefs[AppPrefsKeys.FAIL_REASON_LAST_UPDATED_DATE] ?: "" }
            .first()
    }

    // Reset fail reasons (clear the stored list)
    suspend fun resetFailReasons() {
        context.dataStore.edit { prefs ->
            prefs[AppPrefsKeys.FAIL_REASON_LIST] = ""
            prefs[AppPrefsKeys.FAIL_REASON_LAST_UPDATED_DATE] = LocalDate.now().toString()
        }
    }

    suspend fun getLastHabitResetDate(): String {
        return context.dataStore.data
            .map { prefs -> prefs[AppPrefsKeys.LAST_HABIT_RESET_DATE] ?: "" }
            .first()
    }

    suspend fun setLastHabitResetDate(date: String) {
        context.dataStore.edit { prefs ->
            prefs[AppPrefsKeys.LAST_HABIT_RESET_DATE] = date
        }
    }

}
