package com.app.sololevelinghabittracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.sololevelinghabittracker.viewmodel.XPViewModel

@Composable
fun AvatarScreen() {
    val viewModel: XPViewModel = viewModel()
    val xp by viewModel.xp.collectAsState()
    val level by viewModel.level.collectAsState()

    val xpPerLevel = 100
    val progress = (xp % xpPerLevel) / xpPerLevel.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Level: $level", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("XP: $xp / ${level * xpPerLevel}")

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(onClick = { viewModel.addXP(10) }) {
                Text("+10 XP (Habit)")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.addXP(15) }) {
                Text("+15 XP (Task/Pomodoro)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.resetXP() }) {
            Text("Reset XP")
        }
    }
}
