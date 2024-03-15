package com.yonasoft.jadedictionary.ui.screens.account.login_signup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.ui.screens.account.AccountViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AccountViewModel,
) {
    val authUI = viewModel.authUI
    val providers = viewModel.providers

    val signInLauncher = rememberLauncherForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        viewModel.onSignInResult(res)
    }

    val signInIntent = authUI.value
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .setLogo(R.drawable.jade_logo)
        .setTheme(R.style.Theme_JadeDictionary)
        .build()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = {
                signInLauncher.launch(signInIntent)
            },

            ) {
            Text(
                text = "Log in",
                textAlign = TextAlign.Center
            )
        }
    }
}