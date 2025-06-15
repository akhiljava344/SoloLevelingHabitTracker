package com.app.sololevelinghabittracker.navigation

sealed class Screen(val route: String, val title: String) {
    object Habits : Screen("habits", "Habits")
    object Focus : Screen("focus", "Focus") // ✅ fixed route
    object Quests : Screen("quests", "Quests")
    object Tasks : Screen("tasks", "Tasks")
    object Avatar : Screen("avatar", "Avatar")
    object Weekly : Screen("weekly", "Weekly")
}
