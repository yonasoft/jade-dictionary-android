package com.yonasoft.jadedictionary.presentation.screens.account

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.presentation.screens.account.login_signup.LoginScreen
import com.yonasoft.jadedictionary.presentation.screens.account.user_profile.UserProfile
import com.yonasoft.jadedictionary.presentation.screens.shared.SharedAppViewModel

@Composable
fun AccountScreen(
    navController: NavController,
    accountViewModel: AccountViewModel = hiltViewModel(),
    sharedAppViewModel: SharedAppViewModel = hiltViewModel(),
) {

    val sharedAppState = sharedAppViewModel.sharedAppState.collectAsStateWithLifecycle()
    val isLoggedIn = sharedAppState.value.auth?.currentUser != null
    val isNetworkAvailable = sharedAppState.value.isNetworkAvailable


    Log.d("AccountScreen", "isLoggedIn: $isLoggedIn")

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoggedIn) {
            UserProfile(
                sharedAppViewModel = sharedAppViewModel,
                accountViewModel = accountViewModel
            )
        } else {
            LoginScreen(
                sharedAppViewModel = sharedAppViewModel,
                accountViewModel = accountViewModel,
            )
        }
    }
}