package com.app.sololevelinghabittracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.sololevelinghabittracker.viewmodel.QuestViewModel
import com.app.sololevelinghabittracker.data.local.entity.Quest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestsScreen(viewModel: QuestViewModel) {

    val quests by viewModel.allQuests.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.addSampleQuests()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mini Quests & Boss") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("🔥 Quests Completed: ${quests.count { it.isCompleted }} / ${quests.size}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(quests.size) { index ->
                    val quest = quests[index]
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (quest.isBoss) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    quest.title,
                                    fontWeight = if (quest.isBoss) FontWeight.Bold else FontWeight.Normal
                                )
                                if (quest.isBoss) {
                                    Text("🗡️ Monthly Boss", style = MaterialTheme.typography.labelMedium)
                                }
                            }

                            Checkbox(
                                checked = quest.isCompleted,
                                onCheckedChange = { viewModel.toggleQuestCompleted(quest) }
                            )
                        }
                    }
                }
            }
        }
    }
}
