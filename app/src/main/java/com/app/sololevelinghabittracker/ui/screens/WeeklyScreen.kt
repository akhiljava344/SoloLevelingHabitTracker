package com.app.sololevelinghabittracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.sololevelinghabittracker.viewmodel.MoodViewModel
import com.app.sololevelinghabittracker.data.local.database.MoodDatabase
import com.app.sololevelinghabittracker.repository.MoodRepository
import com.app.sololevelinghabittracker.viewmodel.MoodViewModelFactory

@Composable
fun WeeklyScreen() {
    val context = LocalContext.current
    val db = MoodDatabase.getDatabase(context)
    val repo = MoodRepository(db.moodDao())
    val viewModel: MoodViewModel = viewModel(factory = MoodViewModelFactory(repo))

    val todayMood by viewModel.todayMood.collectAsState()
    val last7Moods by viewModel.last7Moods.collectAsState()

    var mood by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val emojis = listOf("😊", "😐", "😢", "😡", "😴") // FIXED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Today's Mood", style = MaterialTheme.typography.headlineSmall)

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(emojis) { emoji -> // FIXED usage
                Button(
                    onClick = { mood = emoji },
                    modifier = Modifier.padding(4.dp)
                ) { Text(emoji) }
            }
        }

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { if (mood.isNotBlank()) viewModel.setMood(mood, note) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Save Mood")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Last 7 Days Mood", style = MaterialTheme.typography.headlineSmall)

        LazyRow {
            items(last7Moods.size) { index ->
                val moodItem = last7Moods[index]
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = moodItem.mood, style = MaterialTheme.typography.headlineLarge)
                    Text(text = moodItem.date)
                }
            }
        }
    }
}
