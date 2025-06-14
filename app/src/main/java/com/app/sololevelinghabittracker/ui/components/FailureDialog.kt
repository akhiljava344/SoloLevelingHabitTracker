package com.app.sololevelinghabittracker.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.app.sololevelinghabittracker.data.local.entity.Habit

@Composable
fun FailureDialog(
    habit: Habit, // The habit that was missed/failed
    onDismiss: () -> Unit // Callback to dismiss the dialog
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Missed Habit Alert!") },
        text = { Text(text = "You didn't complete '${habit.title}' today. Your streak for this habit is reset!") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}