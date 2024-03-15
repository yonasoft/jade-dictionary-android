package com.yonasoft.jadedictionary.ui.components.account.user_display_setting_card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yonasoft.jadedictionary.R

@Composable
fun UserDisplaySettingCard(
    isEditDisplayName: Boolean,
    currentDisplayName: String,
    displayNameField: String,
    onDisplayNameFieldChange: (text: String) -> Unit,
    onCancelEdit: () -> Unit,
    onEdit: () -> Unit,
    currentProfileImageLink: String? = "",
//    uploadedProfileImage:String,
//    onSave: () -> Unit,
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

            if (!currentProfileImageLink.isNullOrEmpty()) {
                AsyncImage(
                    modifier = Modifier.size(200.dp),
                    model = currentProfileImageLink,
                    contentDescription = "User Profile Image"
                )
            } else {
                Image(
                    modifier = Modifier.size(200.dp),
                    painter = painterResource(id = R.drawable.baseline_account_box_200),
                    contentDescription = "Default Profile Image"
                )
            }
            TextButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = "Upload Icon",
                )
                Text(text = "Upload Profile Image")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = { /*TODO*/ }) {
                Text(text = "Save")
            }
        }
    }
}