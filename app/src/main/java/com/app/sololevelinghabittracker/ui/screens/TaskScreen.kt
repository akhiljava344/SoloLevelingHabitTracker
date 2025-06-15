package com.app.sololevelinghabittracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.sololevelinghabittracker.data.local.database.TaskDatabase
import com.app.sololevelinghabittracker.data.local.entity.Task
import com.app.sololevelinghabittracker.repository.TaskRepository
import com.app.sololevelinghabittracker.viewmodel.TaskViewModel
import androidx.compose.ui.platform.LocalContext
import com.app.sololevelinghabittracker.viewmodel.TaskViewModelFactory

@Composable
fun TaskScreen() {
    val context = LocalContext.current
    val db = TaskDatabase.getDatabase(context)
    val repo = TaskRepository(db.taskDao())
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(repo))

    val activeTasks by viewModel.activeTasks.collectAsState()

    var title by remember { mutableStateOf("") }
    var isUrgent by remember { mutableStateOf(false) }
    var isImportant by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (title.isNotBlank()) {
                        viewModel.addTask(title, isUrgent, isImportant)
                        title = ""
                        isUrgent = false
                        isImportant = false
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row {
            Checkbox(
                checked = isUrgent,
                onCheckedChange = { isUrgent = it }
            )
            Text("Urgent")

            Spacer(modifier = Modifier.width(16.dp))

            Checkbox(
                checked = isImportant,
                onCheckedChange = { isImportant = it }
            )
            Text("Important")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(activeTasks.size) { index ->
                val task = activeTasks[index]
                TaskItem(task, onToggleDone = { viewModel.toggleTaskDone(task) })
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggleDone: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onToggleDone() }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(task.title, modifier = Modifier.weight(1f))
            Text(if (task.isDone) "✅" else "❌")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                when {
                    task.isUrgent && task.isImportant -> "🔥 Do Now"
                    task.isUrgent && !task.isImportant -> "⚡ Delegate"
                    !task.isUrgent && task.isImportant -> "📝 Plan"
                    else -> "🗑️ Eliminate"
                }
            )
        }
    }
}
