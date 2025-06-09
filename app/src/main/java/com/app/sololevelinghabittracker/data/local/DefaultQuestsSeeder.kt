package com.app.sololevelinghabittracker.data.local

import com.app.sololevelinghabittracker.data.local.entity.Quest

object DefaultQuestsSeeder {
    val defaultQuests = listOf(
        Quest(title = "Drink 2L Water"),
        Quest(title = "Read 10 Pages"),
        Quest(title = "30-min Walk"),
        Quest(title = "Slay the Monthly Boss", isBoss = true)
    )
}
