package com.app.sololevelinghabittracker.data.local.dao

import androidx.room.*
import com.app.sololevelinghabittracker.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE isDone = 0")
    fun getAllActiveTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isDone = 1")
    fun getArchivedTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}
