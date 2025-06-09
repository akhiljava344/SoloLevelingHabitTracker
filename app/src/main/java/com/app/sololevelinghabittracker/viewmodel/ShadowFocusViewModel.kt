package com.app.sololevelinghabittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShadowFocusViewModel(
    private val appPreferencesManager: AppPreferencesManager
) : ViewModel() {

    private val totalSeconds = 25 * 60
    private var timerJob: Job? = null
    private var currentSeconds = totalSeconds

    private val _timeText = MutableStateFlow("25:00")
    val timeText: StateFlow<String> = _timeText

    private val _progress = MutableStateFlow(1f)
    val progress: StateFlow<Float> = _progress

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _xpEarned = MutableStateFlow(0)
    val xpEarned: StateFlow<Int> = _xpEarned

    private val _sessionsCompletedToday = MutableStateFlow(0)
    val sessionsCompletedToday: StateFlow<Int> = _sessionsCompletedToday

    init {
        // 🧠 Load initial value of sessions completed today
        viewModelScope.launch {
            appPreferencesManager.completedSessionsToday.collectLatest { count ->
                _sessionsCompletedToday.value = count
            }
        }
    }

    fun startTimer(isShieldMode: Boolean) {
        if (_isRunning.value) return

        _isRunning.value = true

        timerJob = viewModelScope.launch {
            while (currentSeconds > 0 && _isRunning.value) {
                delay(1000)
                currentSeconds--

                _timeText.value = formatTime(currentSeconds)
                _progress.value = currentSeconds / totalSeconds.toFloat()
            }

            if (currentSeconds == 0) {
                val xp = if (isShieldMode) 20 else 10
                _xpEarned.value = xp

                // 🧠 Store session complete in DataStore
                appPreferencesManager.incrementSessionCount()
            }

            _isRunning.value = false
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        timerJob?.cancel()
        currentSeconds = totalSeconds
        _timeText.value = formatTime(totalSeconds)
        _progress.value = 1f
        _isRunning.value = false
        _xpEarned.value = 0
    }

    private fun formatTime(seconds: Int): String {
        val minutesPart = seconds / 60
        val secondsPart = seconds % 60
        return String.format("%02d:%02d", minutesPart, secondsPart)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
