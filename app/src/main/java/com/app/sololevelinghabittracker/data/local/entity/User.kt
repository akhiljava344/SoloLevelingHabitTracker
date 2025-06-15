// --- User.kt ---
package com.app.sololevelinghabittracker.data.local.entity // Package should match your existing entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate // Make sure to import LocalDate

/**
 * Represents the player's overall character in the Solo Leveling Habit Tracker.
 * This entity stores the user's total experience points (XP), current level,
 * the type of avatar PNG they currently have unlocked/selected, and tracks
 * the last date for end-of-day processing.
 *
 * This entity consolidates global user data that was previously spread across
 * design documents and potentially other temporary entities like UserStats.
 */
@Entity(tableName = "users") // Defines this class as a Room entity with a table name "users"
data class User(
    @PrimaryKey val id: Int = 1, // Default ID to 1 as there's typically only one user in a local app
    var totalXp: Int = 0, // Total experience points accumulated by the user
    var level: Int = 0, // Current level of the user, starts from 0.
    var avatarType: String = "default_avatar", // String to reference the current avatar PNG (e.g., "default", "tier1", etc.)
    var lastResetDate: LocalDate = LocalDate.MIN // To track the last date when end-of-day processing was performed.
)