package com.app.sololevelinghabittracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.sololevelinghabittracker.data.entity.Habit
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel

@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel,
    contentPadding: PaddingValues,
    onAddHabitClick: () -> Unit
) {
    val habits by viewModel.allHabits.collectAsState(initial = emptyList())

    // Group habits by category
    val habitsByCategory = habits.groupBy { it.category }

    LazyColumn(
        contentPadding = contentPadding,
        modifier = Modifier.fillMaxSize()
    ) {
        habitsByCategory.forEach { (category, habitsInCategory) ->
            item {
                CategorySection(category = category, habits = habitsInCategory, viewModel = viewModel)
            }
        }

        // Spacer for Add Button
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Floating Add Button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(onClick = { onAddHabitClick() }) {
            Text("+")
        }
    }
}

@Composable
fun CategorySection(
    category: String,
    habits: List<Habit>,
    viewModel: HabitsViewModel
) {
    val categoryColors = mapOf(
        "Work" to Color(0xFF4CAF50),
        "Health" to Color(0xFFF44336),
        "Personal" to Color(0xFF3F51B5),
        "General" to Color(0xFF9E9E9E)
    )
    val sectionColor = categoryColors[category] ?: Color.Gray

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .background(sectionColor.copy(alpha = 0.1f))
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )

        habits.forEach { habit ->
            HabitItem(habit = habit, viewModel = viewModel)
        }
    }
}

@Composable
fun HabitItem(habit: Habit, viewModel: HabitsViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = habit.title, style = MaterialTheme.typography.titleMedium)
                Text(text = "🔥 Streak: ${habit.streak} ")
                Text(text = "XP: ${habit.xp} ✨")
            }
            Checkbox(
                checked = habit.checked,
                onCheckedChange = {
                    viewModel.toggleHabit(habit)
                }
            )
        }
    }
}

/**
 * Returns dynamic streak icon based on streak count.
 */
fun getStreakIcon(streak: Int): String {
    return when {
        streak <= 1 -> "🔥"
        streak in 2..3 -> "🔥🔥"
        streak in 4..5 -> "🔥🔥🔥"
        streak >= 6 -> "🌟🔥🔥🔥"
        else -> "🔥"
    }
}
