package com.app.sololevelinghabittracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "moods")
data class Mood(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String = LocalDate.now().toString(),
    val mood: String,
    val note: String = ""
)
