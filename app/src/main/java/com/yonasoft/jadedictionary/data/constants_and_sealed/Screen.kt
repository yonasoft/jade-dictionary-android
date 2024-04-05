package com.yonasoft.jadedictionary.data.constants_and_sealed

sealed class Screen(val route: String, val name:String? = null) {
    data object Search : Screen(route = "search")
    data object Lists : Screen(route = "lists")
    data object AddList : Screen(route = "add-lists")
    data object WordList : Screen("word_list/{wordListId}") {
        fun createRoute(wordListId: String) = "word_list/$wordListId"
    }
    data object Practice: Screen(route = "practice")
    data object Account: Screen(route = "account")
    data object Settings: Screen(route = "settings")
    data object Support: Screen(route = "support")
    data object FAQ: Screen(route = "faq", name="FAQ")
    data object Contact: Screen(route = "contact", name = "Contact Us")
    data object About: Screen(route = "about", name = "About")
    data object Donate: Screen(route = "donate", name="Donate")



}