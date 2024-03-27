package com.yonasoft.jadedictionary.presentation.components.dialogs.account_deletion_confirm_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun AccountDeletionConfirmDialog(
    showDialog: MutableState<Boolean>,
    confirmationText: MutableState<String>,
    showError:MutableState<Boolean>,
    onDelete: () -> Unit
) {

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                showError.value = false // Reset error state on dismiss
            },
            title = { Text("Are you sure?") },
            text = {
                Column {
                    Text("Are you sure you want to delete your account? This action cannot be reverted. To confirm, please type: \"Delete Account\"")
                    OutlinedTextField(
                        value = confirmationText.value,
                        onValueChange = {
                            confirmationText.value = it
                            showError.value = false // Reset error on text change
                        },
                        label = { Text("Confirmation Text") },
                        modifier = Modifier.padding(top = 8.dp),
                        isError = showError.value
                    )
                    if (showError.value) {
                        Text(
                            "Confirmation text does not match.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (confirmationText.value == "Delete Account") {
                            onDelete()
                            showDialog.value = false
                            confirmationText.value = ""
                            showError.value = false
                        } else {
                            showError.value = true
                        }
                    }
                ) {
                    Text("Confirm")
                }
            }
,
            dismissButton = {
                Button(onClick = {
                    showDialog.value = false
                    showError.value = false // Reset error state on cancel
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

