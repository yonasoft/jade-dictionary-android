package com.yonasoft.jadedictionary.data.constants

sealed class Screen(val route: String) {
    data object Search : Screen(route = "search")
    data object Lists : Screen(route = "lists")
    data object Account: Screen(route = "account")
}