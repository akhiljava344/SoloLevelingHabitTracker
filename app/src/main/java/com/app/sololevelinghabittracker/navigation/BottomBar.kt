package com.app.sololevelinghabittracker.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.sololevelinghabittracker.R

data class BottomNavItem(
    val screen: Screen,
    val icon: Painter
)

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(Screen.Habits, painterResource(id = R.drawable.ic_habits)),
        BottomNavItem(Screen.ShadowFocus, painterResource(id = R.drawable.ic_focus)),
        BottomNavItem(Screen.Quests, painterResource(id = R.drawable.ic_quests))
    )

    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(painter = item.icon, contentDescription = item.screen.title) },
                label = { Text(item.screen.title) }
            )
        }
    }
}
