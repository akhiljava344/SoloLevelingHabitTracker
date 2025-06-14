package com.app.sololevelinghabittracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.sololevelinghabittracker.data.local.entity.Quest
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: Quest)

    @Update
    suspend fun updateQuest(quest: Quest)

    @Delete
    suspend fun deleteQuest(quest: Quest)

    @Query("SELECT * FROM quests")
    fun getAllQuests(): Flow<List<Quest>>

    @Query("SELECT * FROM quests WHERE id = :id LIMIT 1")
    suspend fun getQuestById(id: Int): Quest?

    // You might also want queries for completed/uncompleted quests
    @Query("SELECT * FROM quests WHERE isCompleted = 1")
    fun getCompletedQuests(): Flow<List<Quest>>

    @Query("SELECT * FROM quests WHERE isCompleted = 0")
    fun getUncompletedQuests(): Flow<List<Quest>>
}