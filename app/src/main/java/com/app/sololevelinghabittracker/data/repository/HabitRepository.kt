package com.app.sololevelinghabittracker.data.repository

import com.app.sololevelinghabittracker.data.local.dao.HabitDao
import com.app.sololevelinghabittracker.data.local.entity.Habit
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class HabitRepository(private val habitDao: HabitDao) {

    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    fun getHabitsForDate(date: String): Flow<List<Habit>> = habitDao.getHabitsForDate(date)

    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)

    suspend fun insertHabits(habits: List<Habit>) = habitDao.insertHabits(habits)

    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    suspend fun resetAllHabitsChecked() = habitDao.resetAllHabitsChecked()

    suspend fun isHabitsForDateEmpty(date: String): Boolean {
        return habitDao.getHabitsForDateOnce(date).isEmpty()
    }
}
