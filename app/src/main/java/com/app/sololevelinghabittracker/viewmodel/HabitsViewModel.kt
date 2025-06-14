package com.app.sololevelinghabittracker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sololevelinghabittracker.data.HabitRepository
import com.app.sololevelinghabittracker.data.local.entity.DailyLog
import com.app.sololevelinghabittracker.data.local.entity.Habit
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager // NEW: Import AppPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine // NEW: For combining flows
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.max

// NEW data class for UI
data class HabitWithCompletionStatus(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val xpAwardedToday: Boolean
)

// Updated HabitsViewModel constructor and logic
class HabitsViewModel(
    private val habitRepository: HabitRepository,
    private val appPreferencesManager: AppPreferencesManager // NEW: Injected AppPreferencesManager
) : ViewModel() {

    // --- StateFlows for UI observation ---
    // Modified: This will now emit HabitWithCompletionStatus
    private val _habitsWithStatus = MutableStateFlow<List<HabitWithCompletionStatus>>(emptyList())
    val habitsWithStatus: StateFlow<List<HabitWithCompletionStatus>> = _habitsWithStatus.asStateFlow()

    private val _userXp = MutableStateFlow(0)
    val userXp: StateFlow<Int> = _userXp.asStateFlow()

    private val _userLevel = MutableStateFlow(1)
    val userLevel: StateFlow<Int> = _userLevel.asStateFlow()

    // Dialog StateFlows
    private val _showLevelUpDialog = MutableStateFlow<Int?>(null)
    val showLevelUpDialog: StateFlow<Int?> = _showLevelUpDialog.asStateFlow()

    private val _showFailureDialog = MutableStateFlow<Habit?>(null)
    val showFailureDialog: StateFlow<Habit?> = _showFailureDialog.asStateFlow()


    init {
        viewModelScope.launch {
            // Collect user XP and Level from DataStore via Repository
            habitRepository.getUserXp().collect { xp ->
                _userXp.value = xp
                val newLevel = calculateLevel(xp)
                if (newLevel > _userLevel.value) {
                    _showLevelUpDialog.value = newLevel // Trigger level up dialog
                }
                _userLevel.value = newLevel
            }
        }

        viewModelScope.launch {
            // Combine all habits with their daily completion status
            habitRepository.getAllHabits().collect { fetchedHabits ->
                val today = LocalDate.now()
                val habitsWithStatusList = mutableListOf<HabitWithCompletionStatus>()
                for (habit in fetchedHabits) {
                    val dailyLog = habitRepository.getDailyLogForHabitAndDate(habit.id, today)
                    habitsWithStatusList.add(
                        HabitWithCompletionStatus(
                            habit = habit,
                            isCompletedToday = dailyLog?.isCompleted ?: false,
                            xpAwardedToday = dailyLog?.xpAwardedToday ?: false
                        )
                    )
                }
                _habitsWithStatus.value = habitsWithStatusList
                Log.d("HabitsVM", "Fetched and combined ${habitsWithStatusList.size} habits with today's status.")
            }
        }

        // This will ensure end-of-day processing runs once per day upon app launch/resume
        processEndOfDayHabits()
    }

    // --- Habit Management ---

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.insertHabit(habit)
            Log.d("HabitsVM", "Added habit: ${habit.title}")
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.updateHabit(habit)
            Log.d("HabitsVM", "Updated habit: ${habit.title}")
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit)
            Log.d("HabitsVM", "Deleted habit: ${habit.title}")
        }
    }

    // --- Core Toggle Logic ---

    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            Log.d("HabitsVM", "--- Toggle called for habit: ${habit.title} (ID: ${habit.id}) ---")

            val today = LocalDate.now()
            // Get the current daily log for today
            val currentDailyLog = habitRepository.getDailyLogForHabitAndDate(habit.id, today)

            val wasCompletedBeforeToggle = currentDailyLog?.isCompleted ?: false
            val wasXpAwardedTodayBeforeToggle = currentDailyLog?.xpAwardedToday ?: false

            Log.d("HabitsVM", "BEFORE TOGGLE: DailyLog exists? ${currentDailyLog != null}, wasCompletedBeforeToggle: $wasCompletedBeforeToggle, wasXpAwardedTodayBeforeToggle: $wasXpAwardedTodayBeforeToggle, Permanent Streak (from Habit obj): ${habit.streak}")

            val newIsCompleted = !wasCompletedBeforeToggle
            val newXpAwardedToday = !wasXpAwardedTodayBeforeToggle

            val updatedDailyLog = currentDailyLog?.copy(
                isCompleted = newIsCompleted,
                xpAwardedToday = newXpAwardedToday
            ) ?: DailyLog(
                habitId = habit.id,
                date = today,
                isCompleted = newIsCompleted,
                xpAwardedToday = newXpAwardedToday
            )

            // Calculate XP change based on the NEW state
            val xpChange = if (updatedDailyLog.isCompleted) {
                // If it's now completed and XP wasn't awarded before
                if (!wasXpAwardedTodayBeforeToggle) {
                    habit.xpGainPerCompletion * habit.streakBonus
                } else {
                    0 // XP already awarded, no change
                }
            } else { // Unchecking the habit
                // If it's now NOT completed and XP WAS awarded before
                if (wasXpAwardedTodayBeforeToggle) {
                    -habit.xpGainPerCompletion * habit.streakBonus
                } else {
                    0 // XP was not awarded, so no XP to revert
                }
            }

            Log.d("HabitsVM", "XP LOGIC: XP change for ${habit.title}: $xpChange")

            // Update user XP if there's a change
            if (xpChange != 0) {
                // Use habitRepository which now uses AppPreferencesManager for user XP
                habitRepository.updateUserXp(_userXp.value + xpChange)
                Log.d("HabitsVM", "User XP updated. New XP: ${_userXp.value + xpChange}")
            }

            // --- Update Habit's XP and Level (for the habit itself) ---
            val newHabitXP = max(0, habit.xp + xpChange) // Ensure habit XP doesn't go below 0
            val newHabitLevel = calculateLevel(newHabitXP) // Assuming a separate leveling for habit XP
            val updatedHabit = habit.copy(
                xp = newHabitXP,
                level = newHabitLevel
            )

            // Save the updated daily log to the repository
            if (currentDailyLog == null) {
                habitRepository.insertDailyLog(updatedDailyLog)
            } else {
                habitRepository.updateDailyLog(updatedDailyLog)
            }
            Log.d("HabitsVM", "DailyLog for habit '${habit.title}' on $today updated: Completed=${updatedDailyLog.isCompleted}, XPAwarded=${updatedDailyLog.xpAwardedToday}")


            // --- Failure Dialog Trigger (Immediate vs. Ignore based on time) ---
            // This part handles the case where the user *actively unchecks* a habit.
            if (!newIsCompleted && wasCompletedBeforeToggle) { // Condition: Transitioning from checked to unchecked
                Log.d("HabitsVM", "DIALOG LOGIC: Unchecking ${habit.title}. Permanent Streak (from Habit obj): ${habit.streak}")

                if (habit.streak > 0) { // Condition: Habit has an active streak
                    val currentTime = LocalTime.now()
                    val tenPM = LocalTime.of(22, 0) // 10:00 PM

                    if (currentTime.isAfter(tenPM)) { // Condition: After 10 PM
                        _showFailureDialog.value = habit // Trigger dialog for this habit
                        Log.d("HabitsVM", "DIALOG LOGIC: Unchecked after 10 PM. Triggering IMMEDIATE failure dialog for ${habit.title}.")
                    } else {
                        Log.d("HabitsVM", "DIALOG LOGIC: Unchecked before/at 10 PM. Ignoring failure dialog for ${habit.title}.")
                        _showFailureDialog.value = null
                    }
                } else {
                    Log.d("HabitsVM", "DIALOG LOGIC: NOT triggering dialog for ${habit.title}: Permanent streak is 0 or less.")
                    _showFailureDialog.value = null
                }
            } else {
                Log.d("HabitsVM", "DIALOG LOGIC: Not triggering dialog for ${habit.title}. Condition (!newIsCompleted && wasCompletedBeforeToggle) not met.")
                _showFailureDialog.value = null // Ensure no dialog if conditions not met
            }

            // Update the habit in the database with its potentially new XP/Level
            habitRepository.updateHabit(updatedHabit)
            Log.d("HabitsVM", "Habit '${habit.title}' updated in DB after toggle (XP/Level potentially changed).")
            Log.d("HabitsVM", "--- Toggle END for habit: ${habit.title} ---")
        }
    }


    // --- New Function for Passive Missed Habit Dialog ---
    fun checkAndTriggerMissedHabitDialog() {
        viewModelScope.launch {
            val currentTime = LocalTime.now()
            val tenPM = LocalTime.of(22, 0)
            val today = LocalDate.now()

            // Only proceed if it's after 10 PM
            if (currentTime.isAfter(tenPM)) {
                Log.d("HabitsVM", "Passive Check: It's after 10 PM. Checking for missed habits.")
                val allHabits = habitRepository.getAllHabits().firstOrNull() ?: emptyList()
                Log.d("HabitsVM", "Passive Check: Found ${allHabits.size} habits in DB.")

                val missedHabit = allHabits.firstOrNull { habit ->
                    // Get today's daily log for this specific habit
                    val dailyLogForToday = habitRepository.getDailyLogForHabitAndDate(habit.id, today)
                    val isCompletedToday = dailyLogForToday?.isCompleted ?: false // Default to false if no log for today

                    // Condition: Habit is unchecked, has streak > 0
                    !isCompletedToday && habit.streak > 0
                }

                if (missedHabit != null) {
                    _showFailureDialog.value = missedHabit // Trigger the dialog
                    Log.d("HabitsVM", "Passive Check: Found missed habit '${missedHabit.title}'. Triggering dialog.")
                } else {
                    _showFailureDialog.value = null
                    Log.d("HabitsVM", "Passive Check: No missed habits found that meet conditions.")
                }
            } else {
                Log.d("HabitsVM", "Passive Check: It's before 10 PM (${currentTime}). Not checking for missed habits.")
                _showFailureDialog.value = null
            }
        }
    }


    // --- Dialog Dismissal Functions ---

    fun dismissLevelUpDialog() {
        _showLevelUpDialog.value = null
        Log.d("HabitsVM", "Level up dialog dismissed.")
    }

    fun dismissFailureDialog() {
        _showFailureDialog.value = null
        Log.d("HabitsVM", "Failure dialog dismissed.")
    }

    // --- Helper Function for Level Calculation ---
    // This is for user level. You'd need a similar one for individual habit levels.
    private fun calculateLevel(xp: Int): Int {
        return when {
            xp >= 1000 -> 10 // Example max level
            xp >= 750 -> 9
            xp >= 500 -> 8
            xp >= 350 -> 7
            xp >= 250 -> 6
            xp >= 170 -> 5
            xp >= 100 -> 4
            xp >= 50 -> 3
            xp >= 20 -> 2
            else -> 1
        }
    }

    // --- End of Day Processing (Keep your existing logic here) ---
    // This function should be called once per day, typically at the start of the app for a new day.
    fun processEndOfDayHabits() {
        viewModelScope.launch {
            // Get last reset date from DataStore via Repository
            val lastResetDate = habitRepository.getLastResetDate().firstOrNull() ?: LocalDate.MIN
            val today = LocalDate.now()

            if (lastResetDate != today) {
                Log.d("HabitsVM", "Processing end-of-day habits. Last reset: $lastResetDate, Today: $today")

                val habitsToUpdate = _habitsWithStatus.value.map { habitWithStatus ->
                    val habit = habitWithStatus.habit
                    val isCompletedToday = habitWithStatus.isCompletedToday

                    if (!isCompletedToday) {
                        // Habit was not completed today, reset streak
                        Log.d("HabitsVM", "Habit '${habit.title}' not completed. Streak reset.")
                        habit.copy(streak = 0)
                    } else {
                        // Habit was completed, increment streak
                        Log.d("HabitsVM", "Habit '${habit.title}' completed. Streak incremented to ${habit.streak + 1}.")
                        habit.copy(streak = habit.streak + 1)
                    }
                }

                habitsToUpdate.forEach { habitRepository.updateHabit(it) }
                // Set last reset date in DataStore via Repository
                habitRepository.setLastResetDate(today)
                Log.d("HabitsVM", "End-of-day processing complete for $today.")
            } else {
                Log.d("HabitsVM", "End-of-day processing already done for $today.")
            }
        }
    }
}