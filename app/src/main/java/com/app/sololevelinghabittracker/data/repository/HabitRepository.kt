package com.app.sololevelinghabittracker.repository

import com.app.sololevelinghabittracker.data.dao.HabitDao
import com.app.sololevelinghabittracker.data.entity.Habit
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {

    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    suspend fun getHabitById(id: Int): Habit? = habitDao.getHabitById(id)
}
