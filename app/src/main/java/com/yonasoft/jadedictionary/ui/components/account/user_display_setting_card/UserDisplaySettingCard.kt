package com.yonasoft.jadedictionary.ui.components.account.user_display_setting_card

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserDisplaySettingCard(
    isEditDisplayName: Boolean,
    currentDisplayName: String,
    displayNameField: String,
    onDisplayNameFieldChange: (text: String) -> Unit,
    onCancelEdit: () -> Unit,
    onEdit: () -> Unit,
    currentProfileImageLink: String? = "",
    selectedImage: Uri?,
    onInitiateUpload: () -> Unit,
    onSave: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            UserDisplayNameSettingColumn(
                isEditDisplayName = isEditDisplayName,
                currentDisplayName = currentDisplayName,
                displayNameField = displayNameField,
                onDisplayNameFieldChange = onDisplayNameFieldChange,
                onCancelEdit = onCancelEdit,
                onEdit = onEdit,
            )
            Spacer(modifier = Modifier.height(6.dp))
            UserDisplayImageSetting(
                currentProfileImageLink = currentProfileImageLink ?: "",
                selectedImage = selectedImage,
                onInitiateUpload = {
                    onInitiateUpload()
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = {
                onSave()
            }) {
                Text(text = "Save")
            }
        }
    }
}