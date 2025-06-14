package com.app.sololevelinghabittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.app.sololevelinghabittracker.data.HabitDatabase
import com.app.sololevelinghabittracker.data.HabitRepository
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager // Import AppPreferencesManager
import com.app.sololevelinghabittracker.ui.HabitsViewModelFactory
import com.app.sololevelinghabittracker.ui.screens.HabitsScreen
import com.app.sololevelinghabittracker.ui.theme.SoloLevelingHabitTrackerTheme
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainActivity : ComponentActivity() {

    private val applicationScope = CoroutineScope(SupervisorJob()) // Define applicationScope here

    // Lazily initialize AppPreferencesManager and HabitDatabase
    private val appPreferencesManager: AppPreferencesManager by lazy {
        AppPreferencesManager(applicationContext)
    }

    private val database: HabitDatabase by lazy {
        HabitDatabase.getDatabase(this, applicationScope) // Pass applicationScope here
    }

    // Lazily initialize HabitRepository
    private val habitRepository: HabitRepository by lazy {
        HabitRepository(database.habitDao(), appPreferencesManager) // Pass appPreferencesManager here
    }

    // Lazily initialize ViewModelFactory and ViewModel
    private val habitsViewModel: HabitsViewModel by viewModels {
        HabitsViewModelFactory(habitRepository, appPreferencesManager) // Pass both to factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoloLevelingHabitTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pass the ViewModel instance to the Composable screen
                    HabitsScreen(viewModel = habitsViewModel)
                }
            }
        }
    }
}