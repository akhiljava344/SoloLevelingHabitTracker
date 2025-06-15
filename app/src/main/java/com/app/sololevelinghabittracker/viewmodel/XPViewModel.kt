package com.app.sololevelinghabittracker.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class XPViewModel : ViewModel() {

    private val _xp = MutableStateFlow(0)
    val xp: StateFlow<Int> = _xp

    private val _level = MutableStateFlow(1)
    val level: StateFlow<Int> = _level

    private val xpPerLevel = 100

    fun addXP(points: Int) {
        val newXP = _xp.value + points
        _xp.value = newXP
        _level.value = (newXP / xpPerLevel) + 1
    }

    fun resetXP() {
        _xp.value = 0
        _level.value = 1
    }
}
