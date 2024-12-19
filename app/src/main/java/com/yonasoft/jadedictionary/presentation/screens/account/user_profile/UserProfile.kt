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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yonasoft.jadedictionary.presentation.components.account.account_deletion_card.AccountDeletionCard
import com.yonasoft.jadedictionary.presentation.components.account.password_Setting_card.PasswordSettingCard
import com.yonasoft.jadedictionary.presentation.components.account.user_display_setting_card.UserDisplaySettingCard
import com.yonasoft.jadedictionary.presentation.components.dialogs.account_deletion_confirm_dialog.AccountDeletionConfirmDialog
import com.yonasoft.jadedictionary.presentation.screens.account.AccountViewModel
import com.yonasoft.jadedictionary.presentation.screens.shared.SharedAppViewModel

@Composable
fun UserProfile(
    sharedAppViewModel: SharedAppViewModel,
    accountViewModel: AccountViewModel,
) {

    val context = LocalContext.current
    val sharedAppState = sharedAppViewModel.sharedAppState.collectAsStateWithLifecycle()
    val auth = sharedAppState.value.auth
    val currentUser = auth!!.currentUser
    val currDisplayName = accountViewModel.currDisplayName.value
    val isEditDisplayName = accountViewModel.isEditDisplayName
    val displayNameField = accountViewModel.displayNameField
    val selectedImage = accountViewModel.selectedImage

    val password = accountViewModel.password
    val confirmPassword = accountViewModel.confirmPassword
    val passwordError = accountViewModel.passwordError
    val passwordVisible = accountViewModel.passwordVisible

    val showDeletionConfirmation = accountViewModel.showDeletionConfirmation
    val confirmationText = accountViewModel.confirmationText
    val showDeleteConfirmationError = accountViewModel.showDeleteConfirmationError

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
                if (auth.currentUser!!.isAnonymous) {
                    accountViewModel.initiateAccountDeletion(auth = auth)
                } else {
                    accountViewModel.signOut {
                        sharedAppViewModel.updateAuthState()
                    }
                }
            }) {
            Text(text = "Log out")
        }

        Spacer(modifier = Modifier.height(12.dp))

        UserDisplaySettingCard(
            isEditDisplayName = isEditDisplayName.value,
            currentDisplayName = currDisplayName,
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
            currentImageLink = currentUser!!.photoUrl.toString(),
            selectedImage = selectedImage.value,
            onInitiateUpload = {
                launcher.launch(
                    PickVisualMediaRequest()
                )
            },
            onSave = {
                accountViewModel.updateDisplayInfo(auth = auth)
            },
        )

        Spacer(modifier = Modifier.height(12.dp))
        if (!currentUser.isAnonymous) {
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
                accountViewModel.savePassword()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AccountDeletionCard {
            showDeletionConfirmation.value = true
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    AccountDeletionConfirmDialog(
        showDialog = showDeletionConfirmation,
        confirmationText = confirmationText,
        showError = showDeleteConfirmationError,
        onDelete = {
            if (confirmationText.value == "Delete Account") {
                accountViewModel.initiateAccountDeletion(auth = auth) { deletionSuccess, errorMessage ->
                    if (deletionSuccess) {
                        showToast(context, "Account successfully deleted.")
                        accountViewModel.signOut{
                            sharedAppViewModel.updateAuthState()
                        }
                    } else {
                        showToast(
                            context,
                            errorMessage ?: "Failed to delete account. Please try again."
                        )
                    }
                }
                showDeletionConfirmation.value = false
            } else {
                showToast(
                    context,
                    "Confirmation text does not match. Please type exactly \"Delete Account\" to confirm."
                )
            }
        }
    )


}


fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
    val toast = Toast.makeText(context, message, duration)
    toast.show()
}
