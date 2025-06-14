package com.app.sololevelinghabittracker.data

import com.app.sololevelinghabittracker.data.local.dao.HabitDao
// REMOVED: import com.app.sololevelinghabittracker.data.local.dao.UserStatsDao // No longer needed
import com.app.sololevelinghabittracker.data.local.entity.DailyLog
import com.app.sololevelinghabittracker.data.local.entity.Habit
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager // NEW: Import AppPreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map // Used for transforming DataStore Flow
import java.time.LocalDate

class HabitRepository(
    private val habitDao: HabitDao,
    private val appPreferencesManager: AppPreferencesManager // NEW: Injected AppPreferencesManager
) {

    // --- Habit Operations ---
    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()
    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    // --- Daily Logs Operations ---
    suspend fun getDailyLogForHabitAndDate(habitId: Int, date: LocalDate): DailyLog? {
        return habitDao.getDailyLogForHabitAndDate(habitId, date)
    }
    suspend fun insertDailyLog(dailyLog: DailyLog) {
        habitDao.insertDailyLog(dailyLog)
    }
    suspend fun updateDailyLog(dailyLog: DailyLog) {
        habitDao.updateDailyLog(dailyLog)
    }

    // --- User Stats (Managed by AppPreferencesManager/DataStore) ---
    fun getUserXp(): Flow<Int> = appPreferencesManager.getUserXp() // Use DataStore
    suspend fun updateUserXp(xp: Int) = appPreferencesManager.saveUserXp(xp) // Use DataStore

    fun getLastResetDate(): Flow<LocalDate?> = appPreferencesManager.getLastResetDate() // Use DataStore
    suspend fun setLastResetDate(date: LocalDate) = appPreferencesManager.saveLastResetDate(date) // Use DataStore
}