package com.app.sololevelinghabittracker.repository

import com.app.sololevelinghabittracker.data.local.dao.TaskDao
import com.app.sololevelinghabittracker.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val activeTasks: Flow<List<Task>> = taskDao.getAllActiveTasks()
    val archivedTasks: Flow<List<Task>> = taskDao.getArchivedTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}
