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
    habitsViewModel: HabitsViewModel,
    questViewModel: QuestViewModel,
    paddingValues: PaddingValues,
    AppPreferencesManager : AppPreferencesManager
) {
    NavHost(navController = navController,
        startDestination = Screen.Habits.route,
        modifier = Modifier.padding(paddingValues) ) {

        composable(Screen.Habits.route) {
            HabitsScreen(
                viewModel = habitsViewModel,
                contentPadding = paddingValues
            )
        }
        composable(Screen.ShadowFocus.route) {
            PomodoroScreen(appPreferencesManager = AppPreferencesManager) // ✅ CORRECT
        }
        composable(Screen.Quests.route) {
            QuestsScreen(viewModel = questViewModel)
        }

    }
}
