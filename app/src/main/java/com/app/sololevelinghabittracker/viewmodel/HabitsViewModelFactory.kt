package com.app.sololevelinghabittracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.sololevelinghabittracker.data.HabitRepository
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager // NEW: Import AppPreferencesManager
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel

class HabitsViewModelFactory(
    private val repository: HabitRepository,
    private val appPreferencesManager: AppPreferencesManager // NEW: Add AppPreferencesManager to constructor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitsViewModel::class.java)) {
            return HabitsViewModel(repository, appPreferencesManager) as T // Pass both dependencies
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}