package com.app.sololevelinghabittracker.data.local.dao

import androidx.room.*
import com.app.sololevelinghabittracker.data.local.entity.Quest
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    @Query("SELECT * FROM quests") // replace with your actual table name
    fun getAllQuests(): Flow<List<Quest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<Quest>)

    @Update
    suspend fun updateQuest(quest: Quest)

    @Query("SELECT COUNT(*) FROM quests")
    suspend fun getQuestCount(): Int

}
