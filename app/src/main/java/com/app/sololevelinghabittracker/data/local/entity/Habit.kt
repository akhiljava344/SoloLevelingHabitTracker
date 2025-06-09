package com.app.sololevelinghabittracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val section: String,
    val date: String,
    val isChecked: Boolean = false,
    val streak: Int = 0,
    val lastCheckedDate: String = "",
    val xp: Int = 0,
    val level: Int = 1
)

