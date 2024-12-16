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
import com.yonasoft.jadedictionary.presentation.components.account.account_deletion_card.AccountDeletionCard
import com.yonasoft.jadedictionary.presentation.components.account.password_Setting_card.PasswordSettingCard
import com.yonasoft.jadedictionary.presentation.components.account.user_display_setting_card.UserDisplaySettingCard
import com.yonasoft.jadedictionary.presentation.components.dialogs.account_deletion_confirm_dialog.AccountDeletionConfirmDialog
import com.yonasoft.jadedictionary.presentation.screens.account.AccountViewModel

@Composable
fun UserProfile(
    viewModel: AccountViewModel,
) {

    val context = LocalContext.current
    val auth = viewModel.auth
    val currentUser = auth.value!!.currentUser
    val currDisplayName = viewModel.currDisplayName.value
    val isEditDisplayName = viewModel.isEditDisplayName
    val displayNameField = viewModel.displayNameField
    val selectedImage = viewModel.selectedImage

    val password = viewModel.password
    val confirmPassword = viewModel.confirmPassword
    val passwordError = viewModel.passwordError
    val passwordVisible = viewModel.passwordVisible

    val showDeletionConfirmation = viewModel.showDeletionConfirmation
    val confirmationText = viewModel.confirmationText
    val showDeleteConfirmationError = viewModel.showDeleteConfirmationError

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
                if(auth.value!!.currentUser!!.isAnonymous){
                    viewModel.initiateAccountDeletion()
                }else {
                    viewModel.signOut()
                }
            }) {
            Text(text = "Log out")
        }

        Spacer(modifier = Modifier.height(12.dp))

        UserDisplaySettingCard(
            isEditDisplayName = isEditDisplayName.value,
            currentDisplayName = currDisplayName,
            displayNameField =  displayNameField.value,
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
                viewModel.updateDisplayInfo()
            },
        )

        Spacer(modifier = Modifier.height(12.dp))
        if (auth.value?.currentUser!=null && !auth.value?.currentUser!!.isAnonymous) {
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
                viewModel.savePassword()
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
                viewModel.initiateAccountDeletion { deletionSuccess, errorMessage ->
                    if (deletionSuccess) {
                        showToast(context, "Account successfully deleted.")
                        viewModel.signOut()
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
