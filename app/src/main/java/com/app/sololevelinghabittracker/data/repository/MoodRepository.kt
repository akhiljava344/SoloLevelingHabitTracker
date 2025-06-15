package com.app.sololevelinghabittracker.repository

import com.app.sololevelinghabittracker.data.local.dao.MoodDao
import com.app.sololevelinghabittracker.data.local.entity.Mood
import kotlinx.coroutines.flow.Flow

class MoodRepository(private val moodDao: MoodDao) {

    fun getMoodByDate(date: String): Flow<Mood?> = moodDao.getMoodByDate(date)

    fun getLast7Moods(): Flow<List<Mood>> = moodDao.getLast7Moods()

    suspend fun insertMood(mood: Mood) {
        moodDao.insertMood(mood)
    }
}
