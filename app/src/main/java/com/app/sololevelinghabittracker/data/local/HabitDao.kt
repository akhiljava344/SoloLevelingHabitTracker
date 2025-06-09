package com.app.sololevelinghabittracker.data.local

import androidx.room.*
import com.app.sololevelinghabittracker.data.local.entity.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE date = :date")
    fun getHabitsForDate(date: String): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(habits: List<Habit>)

    @Update
    suspend fun updateHabit(habit: Habit)

}
