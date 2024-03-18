package com.yonasoft.jadedictionary.presentation.components.account.email_setting_card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmailSettingCard(
    currentEmail: String,
    email: String,
    confirmEmail: String,
    onEmailChange: (text: String) -> Unit,
    onConfirmEmailChange: (text: String) -> Unit,
    emailError: String? = null,
    onSave: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()

            .padding(12.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Change Email",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Text(
                text = "Current Email: $currentEmail",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            if (emailError != null) {
                Text(
                    text = emailError,
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            TextField(
                value = email,
                onValueChange = { onEmailChange(it) },
                placeholder = { Text(text = "New Email") }
            )
            TextField(
                value = confirmEmail,
                onValueChange = { onConfirmEmailChange(it) },
                placeholder = { Text(text = "Confirm New Email") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = {
                onSave()
            }) {
                Text(text = "Save")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}