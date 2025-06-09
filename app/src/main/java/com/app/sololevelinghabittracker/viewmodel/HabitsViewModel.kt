package com.app.sololevelinghabittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.sololevelinghabittracker.data.local.entity.Habit
import com.app.sololevelinghabittracker.data.repository.HabitRepository
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitsViewModel(
    private val repository: HabitRepository,
    private val prefsManager: AppPreferencesManager
) : ViewModel() {

    private val today = LocalDate.now().toString()

    val habits: StateFlow<Map<String, List<Habit>>> = repository.getHabitsForDate(today)
        .map { list -> list.groupBy { it.section } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    // State to trigger Failure Reason Dialog in UI
    private val _showFailReasonDialog = MutableStateFlow<Habit?>(null)
    val showFailReasonDialog: StateFlow<Habit?> = _showFailReasonDialog.asStateFlow()

    init {
        viewModelScope.launch {
            val lastResetDate = prefsManager.getLastHabitResetDate()
            if (lastResetDate != today) {
                repository.resetAllHabitsChecked()
                prefsManager.setLastHabitResetDate(today)
            }

            val isEmpty = repository.isHabitsForDateEmpty(today)
            if (isEmpty) {
                seedDefaultHabits()
            }
        }
    }

    private suspend fun seedDefaultHabits() {
        val defaultHabits = listOf(
            Habit(title = "Wake Early", section = "Morning Routine", date = today),
            Habit(title = "Meditate", section = "Morning Routine", date = today),
            Habit(title = "Drink Water", section = "Morning Routine", date = today),
            Habit(title = "Plan", section = "Work Routine", date = today),
            Habit(title = "Deep Work", section = "Work Routine", date = today),
            Habit(title = "Take Breaks", section = "Work Routine", date = today),
            Habit(title = "2hrs Study", section = "Learning & Habits", date = today),
            Habit(title = "Read", section = "Learning & Habits", date = today),
            Habit(title = "Clean Breakfast", section = "Food Log", date = today),
            Habit(title = "Clean Lunch", section = "Food Log", date = today),
            Habit(title = "Clean Dinner", section = "Food Log", date = today),
            Habit(title = "<3h Phone", section = "Digital Discipline", date = today),
            Habit(title = "No Social Media", section = "Digital Discipline", date = today),
            Habit(title = "Sleep Before 10:30", section = "Night Routine", date = today),
            Habit(title = "Journal", section = "Night Routine", date = today)
        )
        repository.insertHabits(defaultHabits)
    }

    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            val todayDate = LocalDate.now()
            val todayStr = todayDate.toString()
            val yesterdayStr = todayDate.minusDays(1).toString()

            val newChecked = !habit.isChecked
            var newStreak = habit.streak
            var newXp = habit.xp
            var newLevel = habit.level
            var lastCheckedDate = habit.lastCheckedDate

            if (newChecked) {
                if (habit.lastCheckedDate != todayStr) {
                    newStreak = if (habit.lastCheckedDate == yesterdayStr) habit.streak + 1 else 1
                    newXp += 10

                    val levelThreshold = newLevel * 100
                    if (newXp >= levelThreshold) {
                        newLevel += 1
                        newXp -= levelThreshold
                    }

                    lastCheckedDate = todayStr
                }
            } else {
                // ❌ User unchecked - check for streak break
                if (habit.lastCheckedDate == yesterdayStr && habit.streak > 0) {
                    // Trigger Failure Reason Dialog
                    _showFailReasonDialog.value = habit
                }
            }

            val updatedHabit = habit.copy(
                isChecked = newChecked,
                streak = newStreak,
                lastCheckedDate = lastCheckedDate,
                xp = newXp,
                level = newLevel
            )

            repository.updateHabit(updatedHabit)
        }
    }

    fun clearFailReasonDialog() {
        _showFailReasonDialog.value = null
    }

    fun saveFailureReason(habit: Habit, reason: String) {
        viewModelScope.launch {
            val todayStr = LocalDate.now().toString()
            val key = "${habit.id}_$todayStr"
            val existingReasons = prefsManager.getFailReasons().toMutableList()
            existingReasons.add("Habit: ${habit.title}, Reason: $reason")
            prefsManager.saveFailReasons(existingReasons)
        }
    }
}

class HabitsViewModelFactory(
    private val repository: HabitRepository,
    private val prefsManager: AppPreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitsViewModel(repository, prefsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
