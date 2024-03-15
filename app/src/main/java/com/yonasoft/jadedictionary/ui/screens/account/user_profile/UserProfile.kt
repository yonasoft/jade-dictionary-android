package com.yonasoft.jadedictionary.ui.screens.account.user_profile

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yonasoft.jadedictionary.ui.components.account.user_display_setting_card.UserDisplaySettingCard
import com.yonasoft.jadedictionary.ui.screens.account.AccountViewModel

@Composable
fun UserProfile(
    viewModel: AccountViewModel,
) {

    val context = LocalContext.current
    val isEditDisplayName = viewModel.isEditDisplayName
    val displayNameField = viewModel.displayNameField
    val currentDisplayName = viewModel.currentUser.value!!.displayName ?: ""


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .scrollable(
                    state = rememberScrollState(
                    ), orientation = Orientation.Vertical
                ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                currentDisplayName = currentDisplayName,
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
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}