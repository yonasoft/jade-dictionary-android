package com.yonasoft.jadedictionary.data.constants

sealed class Screen(val route: String) {
    data object Search : Screen(route = "search")
    data object Lists : Screen(route = "lists")
    data object AddList : Screen(route = "add-lists")
    object WordList : Screen("word_list/{wordListId}") {
        fun createRoute(wordListId: Int) = "word_list/$wordListId"
    }
    data object Account: Screen(route = "account")
}