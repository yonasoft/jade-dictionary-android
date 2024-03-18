package com.yonasoft.jadedictionary.presentation.components.account.user_display_setting_card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserDisplayNameSettingColumn(
    isEditDisplayName: Boolean,
    currentDisplayName: String,
    displayNameField: String,
    onDisplayNameFieldChange: (text: String) -> Unit,
    onCancelEdit: () -> Unit,
    onEdit: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Display Name",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        if (isEditDisplayName) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),

                value = displayNameField,
                onValueChange = {
                    onDisplayNameFieldChange(it)
                },
                placeholder = { Text(text = "New Display Name") },
                trailingIcon =
                {
                    IconButton(
                        onClick = {
                            onCancelEdit()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancel Icon",
                        )
                    }
                }
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text =
                    currentDisplayName,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                )
                IconButton(
                    onClick = {
                        onEdit()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Icon",
                    )
                }
            }
        }
    }
}