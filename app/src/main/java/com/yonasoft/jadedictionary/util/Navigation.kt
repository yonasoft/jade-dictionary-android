package com.yonasoft.jadedictionary.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yonasoft.jadedictionary.data.constants.Screen
import com.yonasoft.jadedictionary.presentation.screens.account.AccountScreen
import com.yonasoft.jadedictionary.presentation.screens.lists.ListsScreen
import com.yonasoft.jadedictionary.presentation.screens.lists.add_word_list_screen.AddWordListScreen
import com.yonasoft.jadedictionary.presentation.screens.lists.word_list_detail.WordListDetailScreen
import com.yonasoft.jadedictionary.presentation.screens.practice.PracticeScreen
import com.yonasoft.jadedictionary.presentation.screens.search.SearchScreen
import com.yonasoft.jadedictionary.presentation.screens.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupNavigation(navController: NavHostController){
    NavHost(navController = navController, startDestination = Screen.Search.route ){
        composable(Screen.Search.route){
            SearchScreen(navController = navController)
        }
        composable(Screen.Lists.route){
            ListsScreen(navController = navController)
        }
        composable(Screen.AddList.route){
            AddWordListScreen(navController = navController)
        }
        composable(
            route = Screen.WordList.route,
            arguments = listOf(
                navArgument("wordListId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Retrieve the wordListId from the arguments
            val wordListId = backStackEntry.arguments?.getString("wordListId") ?: 0 // Provide default value or handle error
            WordListDetailScreen(navController = navController, wordListId = wordListId.toString())
        }
        composable(Screen.Practice.route){
           PracticeScreen(navController = navController)
        }
        composable(Screen.Account.route){
            AccountScreen(navController = navController)
        }
        composable(Screen.Settings.route){
            SettingsScreen(navController = navController)
        }
    }
}