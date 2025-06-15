package com.app.sololevelinghabittracker.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager
import com.app.sololevelinghabittracker.ui.screens.*
import com.app.sololevelinghabittracker.viewmodel.HabitsViewModel
import com.app.sololevelinghabittracker.viewmodel.QuestViewModel

@Composable
fun NavGraph(
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
                onAddHabitClick = { navController.navigate("add_edit_habit") }
            )
        }

        composable(Screen.Focus.route) {
            PomodoroScreen()
        }

        composable(Screen.Quests.route) {
            QuestsScreen(viewModel = questViewModel)
        }
        composable("add_edit_habit") {
            AddEditHabitScreen(
                habitsViewModel = habitsViewModel,
                onSave = { navController.popBackStack() }
            )
        }
        composable("tasks") {
            // Dummy or actual Tasks Screen
            Text("Tasks Screen Coming Soon") // Replace with actual screen later
        }
        composable("avatar") {
            // Dummy or actual Avatar Screen
            Text("Avatar Screen Coming Soon")
        }
        composable("weekly") {
            // Dummy or actual Weekly Screen
            Text("Weekly Screen Coming Soon")
        }

    }
}
