package com.app.sololevelinghabittracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.sololevelinghabittracker.data.dao.HabitDao
import com.app.sololevelinghabittracker.data.entity.Habit

@Database(entities = [Habit::class], version = 3, exportSchema = false)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val MIGRATION_1_2 = object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE habits ADD COLUMN lastCheckedDate TEXT NOT NULL DEFAULT ''")
                    }
                }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_db"
                )
                    .addMigrations(MIGRATION_1_2) // ✅ Add this
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
