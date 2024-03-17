package com.yonasoft.jadedictionary.ui.screens.account.login_signup

import android.util.Log
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
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.ui.screens.account.AccountViewModel
import kotlin.math.log

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AccountViewModel,
) {
    val providers = viewModel.providers

    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) { res ->
        Log.d("sign_in", "start 2.5")
        viewModel.onSignInResult(res)
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
                Log.d("sign_in", "start")
                try {
                    signInLauncher.launch(signInIntent)
                    Log.d("sign_in", "start2")
                } catch (e: Exception) {
                    Log.e("sign_in", "Sign-in launch failed", e)
                }
            },
        ) {
            Text(
                text = "Log in",
                textAlign = TextAlign.Center
            )
        }
    }
}