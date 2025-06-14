package com.app.sololevelinghabittracker.data.repository

import com.app.sololevelinghabittracker.data.local.DailyHabitLog
import com.app.sololevelinghabittracker.data.local.dao.DailyHabitLogDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class DailyHabitLogRepository(private val dailyHabitLogDao: DailyHabitLogDao) {

    suspend fun insertDailyHabitLog(log: DailyHabitLog) {
        dailyHabitLogDao.insertDailyHabitLog(log)
    }

    suspend fun updateDailyHabitLog(log: DailyHabitLog) {
        dailyHabitLogDao.updateDailyHabitLog(log)
    }

    suspend fun getDailyHabitLogForHabitAndDate(habitId: Int, date: LocalDate): DailyHabitLog? { // Date parameter is LocalDate
        return dailyHabitLogDao.getDailyHabitLogForHabitAndDate(habitId, date)
    }

    suspend fun getLastDailyHabitLog(habitId: Int): DailyHabitLog? {
        return dailyHabitLogDao.getLastDailyHabitLog(habitId)
    }

    fun getAllDailyLogsForDate(date: LocalDate): Flow<List<DailyHabitLog>> { // Date parameter is LocalDate
        return dailyHabitLogDao.getAllDailyLogsForDate(date)
    }

    fun getAllDailyHabitLogsForDate(date: LocalDate): Flow<List<DailyHabitLog>> =
        dailyHabitLogDao.getAllDailyHabitLogsForDate(date)
}