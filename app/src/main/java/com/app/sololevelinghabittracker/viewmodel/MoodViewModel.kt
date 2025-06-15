package com.app.sololevelinghabittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sololevelinghabittracker.data.local.entity.Mood
import com.app.sololevelinghabittracker.repository.MoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class MoodViewModel(private val repository: MoodRepository) : ViewModel() {

    val today = LocalDate.now().toString()

    val todayMood = repository.getMoodByDate(today).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val last7Moods = repository.getLast7Moods().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun setMood(mood: String, note: String) {
        viewModelScope.launch {
            repository.insertMood(Mood(date = today, mood = mood, note = note))
        }
    }
}
