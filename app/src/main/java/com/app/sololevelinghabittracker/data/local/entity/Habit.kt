package com.app.sololevelinghabittracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

// Main Habit entity
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val section: String,
    val description: String = "",
    val xpGainPerCompletion: Int = 10,
    val streakBonus: Int = 1,
    val level: Int = 1, // Current level of the habit
    val xp: Int = 0, // Current XP of the habit within its level
    val streak: Int = 0, // Permanent streak (e.g., total days completed in a row)
    val creationDate: LocalDate = LocalDate.now(),
    val isEnabled: Boolean = true
) {
    // This is a helper property for the UI, not stored in the database directly.
    // It looks up today's completion status from the dailyLogs (which come from the DB relation).
    // Note: For this to work, your Habit DAO needs to fetch DailyLogs correctly.
    // We'll address how daily logs are fetched/stored when we get to the DAO and Repository.
    // For now, assume this `dailyLogs` property will be populated when you fetch habits.
    // If you are storing dailyLogs *within* the Habit entity using TypeConverters (as previously
    // provided for `List<DailyLog>`), then this `dailyLogs` property would be part of the `Habit`
    // data class and handled by Room's TypeConverters.
    // If DailyLog is a separate entity linked by a foreign key, we need a @Relation in Habit.
    // Given the errors, I will define `DailyLog` as a separate entity below and link via @Relation
    // in the DAO queries later, so this `isChecked` property needs to be computed differently or
    // the UI needs to get it from the DAO.
    // For simplicity with `toggleHabit` and `checkAndTriggerMissedHabitDialog`, we'll pass
    // the full Habit object and query DailyLog separately in the ViewModel.
    // So, this `isChecked` helper can be removed from `Habit` data class.
}

// DailyLog entity - This will be stored as a separate table and linked by habitId
@Entity(tableName = "daily_logs", primaryKeys = ["habitId", "date"])
data class DailyLog(
    val habitId: Int, // Foreign key to Habit
    val date: LocalDate,
    val isCompleted: Boolean,
    val xpAwardedToday: Boolean = false // Track if XP was awarded for this completion today
)

// UserStats entity - For storing global user XP and last reset date
@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1, // Only one row for user stats
    val xp: Int = 0,
    val lastResetDate: LocalDate = LocalDate.MIN // To track end-of-day processing
)