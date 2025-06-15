package com.app.sololevelinghabittracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.sololevelinghabittracker.data.entity.Habit
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel

@Composable
fun AddEditHabitScreen(
    habitsViewModel: HabitsViewModel,
    habitToEdit: Habit? = null,
    onSave: () -> Unit
) {
    var title by remember { mutableStateOf(habitToEdit?.title ?: "") }
    var category by remember { mutableStateOf(habitToEdit?.category ?: "General") }

    val categoryColors = mapOf(
        "Work" to Color(0xFF4CAF50),
        "Health" to Color(0xFFF44336),
        "Personal" to Color(0xFF3F51B5),
        "General" to Color(0xFF9E9E9E)
    )
    val colorHex = categoryColors[category]?.toString() ?: Color.Gray.toString()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.LightGray.copy(alpha = 0.1f)), // Light background
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Add/Edit Habit", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Habit Title") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Dropdown
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(category)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoryColors.keys.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            category = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            val newHabit = Habit(
                id = habitToEdit?.id ?: 0,
                title = title,
                category = category,
                color = colorHex,
                checked = habitToEdit?.checked ?: false,
                streak = habitToEdit?.streak ?: 0,
                xp = habitToEdit?.xp ?: 0,
                skipToday = habitToEdit?.skipToday ?: false
            )
            if (habitToEdit == null) {
                habitsViewModel.insertHabit(newHabit)
            } else {
                habitsViewModel.updateHabit(newHabit)
            }
            onSave()
        }) {
            Text("Save Habit")
        }
    }
}
