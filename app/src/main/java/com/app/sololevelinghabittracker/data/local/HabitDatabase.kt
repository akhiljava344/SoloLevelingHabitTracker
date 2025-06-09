package com.app.sololevelinghabittracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.sololevelinghabittracker.data.local.entity.Habit
import com.app.sololevelinghabittracker.data.local.entity.Quest // ✅ corrected import
import com.app.sololevelinghabittracker.data.local.dao.HabitDao
import com.app.sololevelinghabittracker.data.local.dao.QuestDao

@Database(
    entities = [
        Habit::class,
        Quest::class // ✅ fixed reference
    ],
    version = 3,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                    .fallbackToDestructiveMigration() // ✅ okay for development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
