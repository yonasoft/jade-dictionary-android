package com.yonasoft.jadedictionary.presentation.components.account.password_Setting_card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PasswordSettingCard(
    password: String,
    confirmPassword: String,
    onPasswordChange: (text: String) -> Unit,
    onConfirmPasswordChange: (text: String) -> Unit,
    passwordVisible: Boolean = false,
    togglePasswordVisible: () -> Unit,
    passwordError: String? = null,
    onSave: () -> Unit,
) {

    val togglePasswordIcon = @Composable{
        val image = if (passwordVisible)
            Icons.Filled.Visibility
        else Icons.Filled.VisibilityOff

        // Localized description for accessibility services
        val description = if (passwordVisible) "Hide password" else "Show password"

        // Toggle button to hide or display password
        IconButton(onClick = { togglePasswordVisible() }) {
            Icon(imageVector = image, description)
        }
    }

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
                text = "Change Password",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            if (passwordError != null) {
                Text(
                    text = passwordError,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            TextField(
                value = password,
                onValueChange = { onPasswordChange(it) },
                placeholder = { Text(text = "New Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = togglePasswordIcon
            )
            TextField(
                value = confirmPassword,
                onValueChange = { onConfirmPasswordChange(it) },
                placeholder = { Text(text = "Confirm New Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = togglePasswordIcon
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
