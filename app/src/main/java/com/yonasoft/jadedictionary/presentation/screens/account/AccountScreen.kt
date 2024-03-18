package com.yonasoft.jadedictionary.presentation.screens.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.presentation.screens.account.login_signup.LoginScreen
import com.yonasoft.jadedictionary.presentation.screens.account.user_profile.UserProfile


@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val currentUser = viewModel.currentUser

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentUser.value == null) {
            LoginScreen(navController = navController, viewModel = viewModel)
        } else {
            UserProfile(viewModel = viewModel)
        }
    }
}