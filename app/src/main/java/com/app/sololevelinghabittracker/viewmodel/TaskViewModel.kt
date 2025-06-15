package com.app.sololevelinghabittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sololevelinghabittracker.data.local.entity.Task
import com.app.sololevelinghabittracker.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val activeTasks = repository.activeTasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val archivedTasks = repository.archivedTasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addTask(title: String, isUrgent: Boolean, isImportant: Boolean) {
        viewModelScope.launch {
            repository.insert(Task(title = title, isUrgent = isUrgent, isImportant = isImportant))
        }
    }

    fun toggleTaskDone(task: Task) {
        val updated = task.copy(isDone = !task.isDone)
        viewModelScope.launch {
            repository.update(updated)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}
