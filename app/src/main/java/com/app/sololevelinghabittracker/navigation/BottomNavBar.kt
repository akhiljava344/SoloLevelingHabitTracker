package com.app.sololevelinghabittracker.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.Habits,
        Screen.Focus,
        Screen.Quests,
        Screen.Tasks,
        Screen.Avatar,
        Screen.Weekly
    )
    val icons = listOf("🏠", "⏳", "🗡️", "🧍", "🎮", "📅")

    val navBackStackEntry = navController.currentBackStackEntryAsState()

    NavigationBar {
        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                label = { Text(screen.title) },
                icon = { Text(icons[index]) },
                selected = navBackStackEntry.value?.destination?.route == screen.route,
                onClick = { navController.navigate(screen.route) }
            )
        }
    }
}
