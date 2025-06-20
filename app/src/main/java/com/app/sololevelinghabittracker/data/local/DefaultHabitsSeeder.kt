package com.app.sololevelinghabittracker.data.local

import android.content.Context
import com.app.sololevelinghabittracker.data.local.entity.Habit
import com.app.sololevelinghabittracker.data.repository.HabitRepository
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager

class DefaultHabitsSeeder(
    private val context: Context,
    private val habitRepository: HabitRepository,
    private val preferencesManager: AppPreferencesManager
) {

    private val defaultHabits = listOf(
        createHabit("Wake Early", "Morning Routine"),
        createHabit("Meditate", "Morning Routine"),
        createHabit("Water", "Morning Routine"),
        createHabit("Plan", "Work Routine"),
        createHabit("Deep Work", "Work Routine"),
        createHabit("Breaks", "Work Routine"),
        createHabit("2hrs Study", "Learning & Habits"),
        createHabit("Read", "Learning & Habits"),
        createHabit("Clean Breakfast", "Food Log"),
        createHabit("Clean Lunch", "Food Log"),
        createHabit("Clean Dinner", "Food Log"),
        createHabit("<3h Phone", "Digital Discipline"),
        createHabit("No Social Media", "Digital Discipline"),
        createHabit("Sleep Before 10:30", "Night Routine"),
        createHabit("Journal", "Night Routine")
    )

    private fun createHabit(title: String, category: String): Habit {
        return Habit(
            title = title,
            section = category, // ✅ 'section' correct
            isChecked = false,
            date = "",
            streak = 0 // ✅ 'streak' correct property
        )
    }

    suspend fun seed() {
        val isFirstLaunchDone = preferencesManager.getIsFirstLaunchDone()
        if (!isFirstLaunchDone) {
            habitRepository.insertHabits(defaultHabits)
            preferencesManager.setIsFirstLaunchDone(true)
        }
    }
}
