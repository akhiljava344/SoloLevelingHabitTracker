package com.app.sololevelinghabittracker.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LevelUpDialog(
    newLevel: Int, // The new user level reached
    onDismiss: () -> Unit // Callback to dismiss the dialog
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Level Up!") },
        text = { Text(text = "Congratulations! You reached Level $newLevel!") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Awesome!")
            }
        }
    )
}