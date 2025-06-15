package com.app.sololevelinghabittracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel
import com.app.sololevelinghabittracker.viewmodel.QuestViewModel

@Composable
fun AppNavigation(
    habitsViewModel: HabitsViewModel,
    questViewModel: QuestViewModel,
    appPreferencesManager: AppPreferencesManager
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            paddingValues = innerPadding,
            habitsViewModel = habitsViewModel,
            questViewModel = questViewModel,
            appPreferencesManager = appPreferencesManager
        )
    }
}
