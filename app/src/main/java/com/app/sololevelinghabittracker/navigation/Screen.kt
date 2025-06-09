package com.app.sololevelinghabittracker.navigation

sealed class Screen(val route: String, val title: String) {
    object Habits : Screen("habits", "Habits")
    object ShadowFocus : Screen("shadow_focus", "Focus")
    // 🔜 You can add more like Quests, Avatar, etc.
    object Quests : Screen("quests", "Quests")

}
