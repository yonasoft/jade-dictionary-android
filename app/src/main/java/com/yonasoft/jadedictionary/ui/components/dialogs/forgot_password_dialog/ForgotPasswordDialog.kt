package com.yonasoft.jadedictionary.ui.components.dialogs.forgot_password_dialog

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yonasoft.jadedictionary.ui.screens.account.AccountViewModel

@Composable
fun ForgotPasswordDialog(viewModel:AccountViewModel, showDialog: MutableState<Boolean>, context: Context) {
    if (showDialog.value) {
        var email by rememberSaveable { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Forgot Password") },
            text = {
                Column {
                    Text("Enter your email address to receive a link to reset your password. Please check your inbox.")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.forgotPassword(context, email)
                    showDialog.value = false
                }) {
                    Text("Send")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
