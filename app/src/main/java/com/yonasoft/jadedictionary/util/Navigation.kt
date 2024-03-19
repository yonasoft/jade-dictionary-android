package com.yonasoft.jadedictionary.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yonasoft.jadedictionary.data.constants.Screen
import com.yonasoft.jadedictionary.presentation.screens.account.AccountScreen
import com.yonasoft.jadedictionary.presentation.screens.lists.ListsScreen
import com.yonasoft.jadedictionary.presentation.screens.lists.add_word_list_screen.AddWordListScreen
import com.yonasoft.jadedictionary.presentation.screens.search.SearchScreen

@Composable
fun setupNavigation(navController: NavHostController){
    NavHost(navController = navController, startDestination = Screen.Search.route ){
        composable(Screen.Search.route){
            SearchScreen()
        }
        composable(Screen.Lists.route){
            ListsScreen(navController = navController)
        }
        composable(Screen.AddList.route){
            AddWordListScreen(navController = navController)
        }
        composable(Screen.Account.route){
            AccountScreen(navController = navController)
        }
    }
}