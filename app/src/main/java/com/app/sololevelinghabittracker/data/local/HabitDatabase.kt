package com.app.sololevelinghabittracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.sololevelinghabittracker.data.local.dao.HabitDao
// REMOVED: import com.app.sololevelinghabittracker.data.local.dao.UserStatsDao // No longer needed
import com.app.sololevelinghabittracker.data.local.entity.DailyLog
import com.app.sololevelinghabittracker.data.local.entity.Habit
// REMOVED: import com.app.sololevelinghabittracker.data.local.entity.UserStats // No longer needed
import com.app.sololevelinghabittracker.data.local.entity.Quest // NEW: Import Quest entity
import com.app.sololevelinghabittracker.data.local.Converters // Corrected import path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Habit::class, DailyLog::class, Quest::class], // Updated: Removed UserStats, Added Quest
    version = 18, // <<<<<<<<<<<<<<<< IMPORTANT: INCREMENT THIS VERSION (e.g., to 18) <<<<<<<<<<<<<<<
    exportSchema = false
)
@TypeConverters(Converters::class) // Apply the Converters
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    // REMOVED: abstract fun userStatsDao(): UserStatsDao // No longer needed as UserStats is removed

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "solo_leveling_habit_tracker_database" // Changed database name for clarity/fresh start
                )
                    .fallbackToDestructiveMigration() // Use for development
                    .addCallback(HabitDatabaseCallback(scope)) // Keep callback for initial data if needed
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Callback can be kept for initial habit/quest data if desired
    private class HabitDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val habitDao = database.habitDao()
                }
            }
        }
    }
}