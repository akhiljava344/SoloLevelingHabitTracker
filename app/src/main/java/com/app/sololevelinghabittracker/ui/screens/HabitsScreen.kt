package com.app.sololevelinghabittracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.sololevelinghabittracker.data.local.entity.Habit
import com.app.sololevelinghabittracker.ui.components.FailureDialog
import com.app.sololevelinghabittracker.ui.components.LevelUpDialog
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel
import com.app.sololevelinghabittracker.viewmodel.HabitWithCompletionStatus // NEW: Import HabitWithCompletionStatus
import java.time.LocalDate // Required for preview

@Composable
fun HabitsScreen(viewModel: HabitsViewModel) {
    // Collect the new StateFlow for habits with completion status
    val habitsWithStatus by viewModel.habitsWithStatus.collectAsState()
    val userXp by viewModel.userXp.collectAsState()
    val userLevel by viewModel.userLevel.collectAsState()

    val showLevelUpDialog by viewModel.showLevelUpDialog.collectAsState()
    val showFailureDialog by viewModel.showFailureDialog.collectAsState()

    // Trigger passive check when the screen is first composed
    LaunchedEffect(Unit) {
        viewModel.checkAndTriggerMissedHabitDialog()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solo Leveling Habit Tracker") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "XP: $userXp", modifier = Modifier.padding(end = 8.dp))
                        Text(text = "Level: $userLevel")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (habitsWithStatus.isEmpty()) {
                Text("No habits yet! Add some to start leveling up.", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(habitsWithStatus) { habitWithStatus ->
                        HabitItem(
                            habitWithStatus = habitWithStatus,
                            onToggle = { habit -> viewModel.toggleHabit(habit) }
                            // Add other actions like onEdit, onDelete here if you have them
                        )
                    }
                }
            }
        }

        // Level Up Dialog
        showLevelUpDialog?.let { newLevel ->
            LevelUpDialog(
                newLevel = newLevel,
                onDismiss = { viewModel.dismissLevelUpDialog() }
            )
        }

        // Failure Dialog
        showFailureDialog?.let { habit ->
            FailureDialog(
                habitTitle = habit.title,
                onDismiss = { viewModel.dismissFailureDialog() }
            )
        }
    }
}

@Composable
fun HabitItem(
    habitWithCompletionStatus: HabitWithCompletionStatus,
    onToggle: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    val habit = habitWithCompletionStatus.habit
    val isCompletedToday = habitWithCompletionStatus.isCompletedToday

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle(habit) } // Pass the original Habit object for toggling
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Section: ${habit.section} | Streak: ${habit.streak} | XP: ${habit.xp} | Level: ${habit.level}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Checkbox(
                checked = isCompletedToday,
                onCheckedChange = { /* Handled by clickable modifier on the Card */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitsScreenPreview() {
    // This preview will not show dynamic data but helps visualize the layout
    val sampleHabits = remember {
        listOf(
            HabitWithCompletionStatus(
                habit = Habit(id = 1, title = "Meditate", section = "Mind", streak = 5, xp = 120, level = 3),
                isCompletedToday = true,
                xpAwardedToday = true
            ),
            HabitWithCompletionStatus(
                habit = Habit(id = 2, title = "Workout", section = "Body", streak = 0, xp = 0, level = 1),
                isCompletedToday = false,
                xpAwardedToday = false
            ),
            HabitWithCompletionStatus(
                habit = Habit(id = 3, title = "Read Book", section = "Knowledge", streak = 10, xp = 250, level = 4),
                isCompletedToday = false,
                xpAwardedToday = false
            )
        )
    }

    // You'd need a mock ViewModel or just static content for a true preview
    // For simplicity, just showing the layout structure.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solo Leveling Habit Tracker") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "XP: 150", modifier = Modifier.padding(end = 8.dp))
                        Text(text = "Level: 2")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleHabits) { habitWithStatus ->
                    HabitItem(
                        habitWithStatus = habitWithStatus,
                        onToggle = { /* No-op for preview */ }
                    )
                }
            }
        }
    }
}