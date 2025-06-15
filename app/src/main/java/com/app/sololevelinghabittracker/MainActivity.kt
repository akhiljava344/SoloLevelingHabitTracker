package com.app.sololevelinghabittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.app.sololevelinghabittracker.data.database.HabitDatabase
import com.app.sololevelinghabittracker.data.local.QuestDatabase
import com.app.sololevelinghabittracker.data.repository.QuestRepository
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager
import com.app.sololevelinghabittracker.navigation.AppNavigation
import com.app.sololevelinghabittracker.repository.HabitRepository
import com.app.sololevelinghabittracker.ui.theme.SoloLevelingHabitTrackerTheme
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModelFactory
import com.app.sololevelinghabittracker.viewmodel.QuestViewModel
import com.app.sololevelinghabittracker.viewmodel.QuestViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appPreferencesManager = AppPreferencesManager(applicationContext)

        // ✅ Room Database and Repositories
        val habitsDao = HabitDatabase.getDatabase(applicationContext).habitDao()
        val habitsRepository = HabitRepository(habitsDao)

        val questDao = QuestDatabase.getDatabase(applicationContext).questDao()
        val questRepository = QuestRepository(questDao)

        // ✅ ViewModels with correct Repositories
        val habitsViewModel = ViewModelProvider(
            this,
            HabitsViewModelFactory(habitsRepository)
        )[HabitsViewModel::class.java]

        val questViewModel = ViewModelProvider(
            this,
            QuestViewModelFactory(questRepository)
        )[QuestViewModel::class.java]

        setContent {
            SoloLevelingHabitTrackerTheme {
                AppNavigation(
                    habitsViewModel = habitsViewModel,
                    questViewModel = questViewModel,
                    appPreferencesManager = appPreferencesManager
                )
            }
        }
    }
}
