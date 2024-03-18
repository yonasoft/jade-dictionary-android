package com.yonasoft.jadedictionary.presentation.screens.account.user_profile

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yonasoft.jadedictionary.presentation.components.account.password_Setting_card.PasswordSettingCard
import com.yonasoft.jadedictionary.presentation.components.account.user_display_setting_card.UserDisplaySettingCard
import com.yonasoft.jadedictionary.presentation.screens.account.AccountViewModel

@Composable
fun UserProfile(
    viewModel: AccountViewModel,
) {

    val context = LocalContext.current
    val isEditDisplayName = viewModel.isEditDisplayName
    val displayNameField = viewModel.displayNameField
    val currDisplayName = viewModel.currDisplayName
    val currentImage = viewModel.currentImage
    val selectedImage = viewModel.selectedImage

    val password = viewModel.password
    val confirmPassword = viewModel.confirmPassword
    val passwordError = viewModel.passwordError
    val passwordVisible = viewModel.passwordVisible

    rememberScrollState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImage.value = uri!!
        }
    )

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier,
            onClick = {
                viewModel.signOut(context)
            }) {
            Text(text = "Log out")
        }

        Spacer(modifier = Modifier.height(12.dp))

        UserDisplaySettingCard(
            isEditDisplayName = isEditDisplayName.value,
            currentDisplayName = currDisplayName.value,
            displayNameField = displayNameField.value,
            onDisplayNameFieldChange = {
                displayNameField.value = it
            },
            onCancelEdit = {
                displayNameField.value = ""
                isEditDisplayName.value = false
            },
            onEdit = {
                isEditDisplayName.value = true
            },
            currentImageLink = currentImage.value,
            selectedImage = selectedImage.value,
            onInitiateUpload = {
                launcher.launch(
                    PickVisualMediaRequest()
                )
            },
            onSave = {
                viewModel.updateDisplayInfo(context = context) {
                    val message =
                        if (it) "Display name already exists!" else "Display info successfully changed!"
                    currDisplayName.value = displayNameField.value
                    showToast(context = context, message = message)
                }
                isEditDisplayName.value = false
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        PasswordSettingCard(
            password = password.value,
            confirmPassword = confirmPassword.value,
            onPasswordChange = {
                password.value = it
            },
            onConfirmPasswordChange = {
                confirmPassword.value = it
            },
            passwordError = passwordError.value,
            passwordVisible = passwordVisible.value,
            togglePasswordVisible = {
                passwordVisible.value = !passwordVisible.value
            }
        ) {
            viewModel.savePassword(context)
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}


fun showToast(context: Context, message: String, duration:Int = Toast.LENGTH_SHORT) {
    val toast = Toast.makeText(context, message, duration)
    toast.show()
}
