package com.yonasoft.jadedictionary.presentation.screens.account.login_signup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.presentation.components.dialogs.forgot_password_dialog.ForgotPasswordDialog
import com.yonasoft.jadedictionary.presentation.screens.account.AccountViewModel
import com.yonasoft.jadedictionary.presentation.screens.shared.SharedAppViewModel

@Composable
fun LoginScreen(
    sharedAppViewModel: SharedAppViewModel,
    accountViewModel: AccountViewModel,
) {
    val context = LocalContext.current
    val providers = accountViewModel.providers
    val sharedAppState = sharedAppViewModel.sharedAppState.collectAsStateWithLifecycle()
    val auth = sharedAppState.value.auth

    val signInLauncher = rememberLauncherForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        accountViewModel.onSignInResult(result = res, auth = auth!!) {
            sharedAppViewModel.updateAuthState()
        }
    }

    val signInIntent = AuthUI.getInstance()
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
            }
        ) {
            Text(text = "Log in", textAlign = TextAlign.Center)
        }
        Button(
            onClick = {
                accountViewModel.signInAnonymously()
            },
        ) {
            Text(text = "Sign In Anonymously")
        }

        Button(onClick = {
            accountViewModel.showForgotPasswordDialog.value = true
        }) {
            Text(text = "Forgot Password?")
        }
    }

    ForgotPasswordDialog(accountViewModel, accountViewModel.showForgotPasswordDialog, context)
}
