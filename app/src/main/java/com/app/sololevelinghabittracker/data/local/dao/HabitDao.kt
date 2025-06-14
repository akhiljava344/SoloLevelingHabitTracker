package com.app.sololevelinghabittracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.sololevelinghabittracker.data.local.entity.DailyLog
import com.app.sololevelinghabittracker.data.local.entity.Habit
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    // Retrieve all habits. Note: DailyLogs for a habit will be fetched separately or within ViewModel.
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Int): Habit?

    // --- DailyLog Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(dailyLog: DailyLog)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDailyLog(dailyLog: DailyLog)

    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getDailyLogForHabitAndDate(habitId: Int, date: LocalDate): DailyLog?

    // You might also want to query all daily logs for a habit if needed
    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId ORDER BY date DESC")
    fun getDailyLogsForHabit(habitId: Int): Flow<List<DailyLog>>
}