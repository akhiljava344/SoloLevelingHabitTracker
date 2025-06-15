package com.app.sololevelinghabittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sololevelinghabittracker.data.entity.Habit
import com.app.sololevelinghabittracker.repository.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitsViewModel(private val repository: HabitRepository) : ViewModel() {

    val allHabits = repository.getAllHabits()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insertHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // 👇 IMPORTANT: This will fix your HabitsScreen error.
    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val isChecked = !habit.checked

            val xp = when {
                // First check of the day
                isChecked && habit.lastCheckedDate != today -> habit.xp + 10

                // Uncheck same day — rollback XP
                !isChecked && habit.lastCheckedDate == today -> habit.xp - 10

                else -> habit.xp // No XP change
            }

            val updatedHabit = habit.copy(
                checked = isChecked,
                streak = if (isChecked) habit.streak + 1 else 0,
                xp = xp,
                lastCheckedDate = if (isChecked) today else ""
            )
            repository.updateHabit(updatedHabit)
        }
    }

}
