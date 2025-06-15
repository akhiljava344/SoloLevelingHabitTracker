package com.app.sololevelinghabittracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.sololevelinghabittracker.data.local.dao.QuestDao
import com.app.sololevelinghabittracker.data.local.entity.Quest

// Room Database class for Quests
@Database(entities = [Quest::class], version = 1, exportSchema = false)
abstract class QuestDatabase : RoomDatabase() {

    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var INSTANCE: QuestDatabase? = null

        fun getDatabase(context: Context): QuestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestDatabase::class.java,
                    "quest_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
