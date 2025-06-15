package com.app.sololevelinghabittracker.data.local.dao

import androidx.room.*
import com.app.sololevelinghabittracker.data.local.entity.Mood
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {

    @Query("SELECT * FROM moods WHERE date = :date LIMIT 1")
    fun getMoodByDate(date: String): Flow<Mood?>

    @Query("SELECT * FROM moods ORDER BY date DESC LIMIT 7")
    fun getLast7Moods(): Flow<List<Mood>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: Mood)
}
