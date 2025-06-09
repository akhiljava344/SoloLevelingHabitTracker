package com.app.sololevelinghabittracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.sololevelinghabittracker.navigation.Screen

@Composable
fun BottomBarNavigation(
    navController: NavHostController,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        listOf(Screen.Habits, Screen.ShadowFocus).forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = { onNavigate(screen.route) },
                label = { Text(screen.title) },
                icon = { Icon(Icons.Default.Star, contentDescription = screen.title) }
            )
        }
    }
}
