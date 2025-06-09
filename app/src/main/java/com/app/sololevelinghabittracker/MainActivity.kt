package com.app.sololevelinghabittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.app.sololevelinghabittracker.data.local.HabitDatabase
import com.app.sololevelinghabittracker.data.repository.HabitRepository
import com.app.sololevelinghabittracker.data.repository.QuestRepository
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager
import com.app.sololevelinghabittracker.navigation.AppNavGraph
import com.app.sololevelinghabittracker.navigation.BottomBar
import com.app.sololevelinghabittracker.ui.theme.SoloLevelingHabitTrackerTheme
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModelFactory
import com.app.sololevelinghabittracker.viewmodel.QuestViewModel
import com.app.sololevelinghabittracker.viewmodel.QuestViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = HabitDatabase.getDatabase(applicationContext)
        val appPreferencesManager = AppPreferencesManager(applicationContext) // ✅ Correct lowercase

        val habitRepository = HabitRepository(db.habitDao())
        val questRepository = QuestRepository(db.questDao())

        val habitsFactory = HabitsViewModelFactory(habitRepository, appPreferencesManager)
        val questFactory = QuestViewModelFactory(questRepository)

        val habitsViewModel: HabitsViewModel by viewModels { habitsFactory }
        val questViewModel: QuestViewModel by viewModels { questFactory }

        setContent {
            SoloLevelingHabitTrackerTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBar(navController = navController)
                    }
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        habitsViewModel = habitsViewModel,
                        questViewModel = questViewModel,
                        paddingValues = innerPadding,
                        appPreferencesManager = appPreferencesManager // ✅ fixed lowercase usage here also
                    )
                }
            }
        }
    }
}
