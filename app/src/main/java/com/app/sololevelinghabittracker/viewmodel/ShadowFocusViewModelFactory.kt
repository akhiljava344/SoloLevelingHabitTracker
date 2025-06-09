package com.app.sololevelinghabittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager

class ShadowFocusViewModelFactory(
    private val appPreferencesManager: AppPreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShadowFocusViewModel::class.java)) {
            return ShadowFocusViewModel(appPreferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
