package com.app.sololevelinghabittracker.data.local.dao

import androidx.room.*
import com.app.sololevelinghabittracker.data.local.DailyHabitLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyHabitLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyHabitLog(log: DailyHabitLog)

    @Update
    suspend fun updateDailyHabitLog(log: DailyHabitLog)

    @Query("SELECT * FROM daily_habit_logs WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getDailyHabitLogForHabitAndDate(habitId: Int, date: LocalDate): DailyHabitLog? // Date parameter is LocalDate

    @Query("SELECT * FROM daily_habit_logs WHERE habitId = :habitId ORDER BY date DESC LIMIT 1")
    suspend fun getLastDailyHabitLog(habitId: Int): DailyHabitLog?

    @Query("SELECT * FROM daily_habit_logs WHERE date = :date")
    fun getAllDailyLogsForDate(date: LocalDate): Flow<List<DailyHabitLog>> // Date parameter is LocalDate

    @Query("SELECT * FROM daily_habit_logs WHERE date = :date")
    fun getAllDailyHabitLogsForDate(date: LocalDate): Flow<List<DailyHabitLog>>
}