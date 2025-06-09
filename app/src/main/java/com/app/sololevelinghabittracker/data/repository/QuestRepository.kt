package com.app.sololevelinghabittracker.data.repository

import com.app.sololevelinghabittracker.data.local.dao.QuestDao
import com.app.sololevelinghabittracker.data.local.entity.Quest
import kotlinx.coroutines.flow.Flow

class QuestRepository(private val questDao: QuestDao) {

    val allQuests: Flow<List<Quest>> = questDao.getAllQuests()

    suspend fun insertQuests(quests: List<Quest>) {
        questDao.insertQuests(quests)
    }

    suspend fun updateQuest(quest: Quest) {
        questDao.updateQuest(quest)
    }

    suspend fun getQuestCount(): Int {
        return questDao.getQuestCount()
    }

}
