package com.app.sololevelinghabittracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.sololevelinghabittracker.data.local.entity.Habit
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager
import android.widget.Toast
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel,
    contentPadding: PaddingValues
) {
    val habitSections by viewModel.habits.collectAsStateWithLifecycle()
    val showDialogHabit by viewModel.showFailReasonDialog.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var failReasonText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today’s Habits", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        if (habitSections.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No habits for today. Add from Settings.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                habitSections.forEach { (section, habits) ->
                    item {
                        val sectionColor = getSectionColor(section)
                        Text(
                            text = section,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = sectionColor, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }

                    items(habits, key = { it.id }) { habit ->
                        HabitItem(habit = habit, onToggle = { viewModel.toggleHabit(habit) })
                    }
                }
            }
        }

        // Failure Reason Dialog
        if (showDialogHabit != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearFailReasonDialog() },
                title = { Text("Why did you miss '${showDialogHabit?.title}' today?") },
                text = {
                    TextField(
                        value = failReasonText,
                        onValueChange = { failReasonText = it },
                        placeholder = { Text("Enter reason") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.saveFailureReason(showDialogHabit!!, failReasonText)
                        Toast.makeText(context, "Reason saved for weekly summary!", Toast.LENGTH_SHORT).show()
                        viewModel.clearFailReasonDialog()
                        failReasonText = ""
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.clearFailReasonDialog()
                        failReasonText = ""
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun getSectionColor(section: String): Color = when (section) {
    "Morning Routine" -> Color(0xFFFFF8E1)
    "Work Routine" -> Color(0xFFE3F2FD)
    "Learning & Habits" -> Color(0xFFF3E5F5)
    "Food Log" -> Color(0xFFFFF3E0)
    "Digital Discipline" -> Color(0xFFFFEBEE)
    "Night Routine" -> Color(0xFFECEFF1)
    else -> MaterialTheme.colorScheme.surface
}

@Composable
fun HabitItem(habit: Habit, onToggle: () -> Unit) {
    val cardColor = MaterialTheme.colorScheme.surfaceVariant

    val streakLabel = when {
        habit.streak >= 7 -> "💯 ${habit.streak} Days Master!"
        habit.streak >= 3 -> "🔥 ${habit.streak} Days Streak"
        habit.streak == 2 -> "🟢 2 Days Started"
        habit.streak == 1 -> "🟢 1 Day Started"
        else -> "❄️ No Streak"
    }

    val xpFraction = habit.xp / (habit.level * 100f)
    val levelText = "Level ${habit.level} • ${habit.xp}/${habit.level * 100} XP"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = habit.isChecked, onCheckedChange = { onToggle() })

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = streakLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = xpFraction.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = levelText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
