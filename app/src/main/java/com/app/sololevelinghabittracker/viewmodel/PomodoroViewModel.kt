package com.app.sololevelinghabittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PomodoroViewModel : ViewModel() {

    private val totalTime = 25 * 60 // 25 mins in seconds

    private val _timeLeft = MutableStateFlow(totalTime)
    val timeLeft: StateFlow<Int> = _timeLeft

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun startTimer() {
        if (_isRunning.value) return

        _isRunning.value = true
        viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.value -= 1
            }
            _isRunning.value = false
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
    }

    fun resetTimer() {
        _timeLeft.value = totalTime
        _isRunning.value = false
    }
}
