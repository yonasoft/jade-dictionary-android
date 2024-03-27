package com.yonasoft.jadedictionary.data.constants

sealed class Screen(val route: String) {
    data object Search : Screen(route = "search")
    data object Lists : Screen(route = "lists")
    data object AddList : Screen(route = "add-lists")
    data object WordList : Screen("word_list/{wordListId}") {
        fun createRoute(wordListId: String) = "word_list/$wordListId"
    }
    data object Practice: Screen(route = "practice")
    data object Account: Screen(route = "account")
    data object Settings: Screen(route = "settings")
}