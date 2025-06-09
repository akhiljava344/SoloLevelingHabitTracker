package com.app.sololevelinghabittracker.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager
import com.app.sololevelinghabittracker.ui.screens.HabitsScreen
import com.app.sololevelinghabittracker.ui.screens.PomodoroScreen
import com.app.sololevelinghabittracker.ui.screens.QuestsScreen
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel
import com.app.sololevelinghabittracker.viewmodel.QuestViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    habitsViewModel: HabitsViewModel,
    questViewModel: QuestViewModel,
    appPreferencesManager: AppPreferencesManager
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Habits.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Habits.route) {
            HabitsScreen(
                viewModel = habitsViewModel,
                contentPadding = paddingValues,
                onMatrixClick = { navController.navigate(Screen.Quests.route) } // ✅ Matrix button opens Tasks tab
            )
        }
        composable(Screen.ShadowFocus.route) {
            PomodoroScreen(appPreferencesManager = appPreferencesManager)
        }
        composable(Screen.Quests.route) {
            QuestsScreen(viewModel = questViewModel)
        }
    }
}
