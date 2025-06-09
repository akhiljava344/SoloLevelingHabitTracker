package com.app.sololevelinghabittracker.data.local.dao

import androidx.room.*
import com.app.sololevelinghabittracker.data.local.entity.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habits WHERE date = :date")
    fun getHabitsForDate(date: String): Flow<List<Habit>>

    @Query("UPDATE habits SET isChecked = 0")
    suspend fun resetAllHabitsChecked()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<Habit>)

    @Query("SELECT * FROM habits WHERE date = :date")
    suspend fun getHabitsForDateOnce(date: String): List<Habit>


}

