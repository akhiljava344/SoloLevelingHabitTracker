package com.app.sololevelinghabittracker.data.local

import androidx.room.Entity
import java.time.LocalDate

@Entity(tableName = "daily_habit_logs", primaryKeys = ["habitId", "date"])
data class DailyHabitLog(
    val habitId: Int,
    val date: LocalDate, // Ensure this is LocalDate
    val isCompleted: Boolean = false,
    val currentStreak: Int = 0, // Ensure this is present
    val moodEmoji: Int? = null,
    val journalEntry: String? = null,
    val xpAwardedToday: Boolean = false
)