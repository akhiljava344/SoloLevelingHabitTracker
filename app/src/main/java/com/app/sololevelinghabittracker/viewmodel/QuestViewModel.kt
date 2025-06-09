package com.app.sololevelinghabittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sololevelinghabittracker.data.local.entity.Quest
import com.app.sololevelinghabittracker.data.repository.QuestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// ViewModel to handle Quest-related logic and data operations
class QuestViewModel(private val repository: QuestRepository) : ViewModel() {

    // Expose all Quests as Flow to be collected in Compose UI
    val allQuests: Flow<List<Quest>> = repository.allQuests

    // Function to add sample Quests into the database
    fun addSampleQuests() {
        viewModelScope.launch {
            val count = repository.getQuestCount()
            if (count == 0) { // Only insert if DB is empty
                val sampleQuests = listOf(
                    Quest(
                        id = 0,
                        title = "Train Strength",
                        isCompleted = false,
                        isBoss = false
                    ),
                    Quest(
                        id = 0,
                        title = "Defeat Dungeon Boss",
                        isCompleted = false,
                        isBoss = true
                    ),
                    Quest(
                        id = 0,
                        title = "Collect Magic Stones",
                        isCompleted = false,
                        isBoss = false
                    )
                )
                repository.insertQuests(sampleQuests)
            }
        }
    }


    // Function to toggle quest completion status (on Checkbox click)
    fun toggleQuestCompleted(quest: Quest) {
        viewModelScope.launch {
            val updatedQuest = quest.copy(isCompleted = !quest.isCompleted)
            repository.updateQuest(updatedQuest)
        }
    }
}
