package com.app.sololevelinghabittracker.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.clickable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.sololevelinghabittracker.data.local.entity.Habit
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel,
    contentPadding: PaddingValues
) {
    val habitSections by viewModel.habits.collectAsStateWithLifecycle()
    val showFailureDialog by viewModel.showFailureDialog.collectAsState()
    val context = LocalContext.current
    val prefsManager = remember { AppPreferencesManager(context) }

    LaunchedEffect(Unit) {
        val lastUpdated = prefsManager.getFailReasonLastUpdatedDate()
        val today = LocalDate.now().toString()
        if (lastUpdated != today) {
            prefsManager.resetFailReasons()
        }
    }

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
    }

    if (showFailureDialog != null) {
        FailureReasonDialog(
            onDismiss = { viewModel.cancelFailureReason() },
            onSave = { reason ->
                viewModel.saveFailureReason(reason)
                Toast.makeText(context, "Failure reason saved!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun FailureReasonDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var selectedReason by remember { mutableStateOf("Felt Lazy") }
    var extraNote by remember { mutableStateOf("") }
    val reasons = listOf("Felt Lazy", "No Time", "Forgot", "Emergency", "Health Issue", "Unexpected Work", "Others")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Why did you miss this habit?") },
        text = {
            Column {
                DropdownMenuBox(
                    items = reasons,
                    selectedItem = selectedReason,
                    onItemSelected = { selectedReason = it }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = extraNote,
                    onValueChange = { extraNote = it },
                    label = { Text("Extra Note (Optional)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val fullReason = "$selectedReason${if (extraNote.isNotBlank()) " - $extraNote" else ""}"
                onSave(fullReason)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DropdownMenuBox(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Select Reason") },
            readOnly = true
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { reason ->
                DropdownMenuItem(
                    text = { Text(reason) },
                    onClick = {
                        onItemSelected(reason)
                        expanded = false
                    }
                )
            }
        }
        Spacer(modifier = Modifier
            .matchParentSize()
            .background(Color.Transparent)
            .clickable { expanded = true })
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
fun HabitItem(
    habit: Habit,
    onToggle: () -> Unit
) {
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
