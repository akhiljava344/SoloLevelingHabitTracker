package com.app.sololevelinghabittracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isUrgent: Boolean,
    val isImportant: Boolean,
    val isDone: Boolean = false
)
