package com.app.sololevelinghabittracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // e.g., "Morning", "Health"
    val color: String,    // hex color for UI
    val checked: Boolean = false,
    val streak: Int = 0,
    val xp: Int = 0,
    val skipToday: Boolean = false,
    val lastCheckedDate: String = ""
)
