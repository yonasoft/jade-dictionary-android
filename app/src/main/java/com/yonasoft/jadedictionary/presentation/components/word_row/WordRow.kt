package com.yonasoft.jadedictionary.presentation.components.word_row

import com.yonasoft.jadedictionary.presentation.components.dialogs.word_detail_dialog.WordDetailDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.data.models.Word

@Composable
fun WordRow(word: Word, onClick: () -> Unit, isDialogOpen: MutableState<Boolean>) {
    var menuExpanded by remember { mutableStateOf(false) }

    // Detail Dialog
    if (isDialogOpen.value) {
        WordDetailDialog(word = word, onDismiss = { isDialogOpen.value = false })
    }

    val traditionalText = if (word.traditional == word.simplified) "" else "(${word.traditional})"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
            .padding(horizontal = 4.dp)
        // Make the entire row clickable to show the word detail
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 100.dp)
                .clickable { onClick() },
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.5f),
                    text = "${word.simplified} $traditionalText",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.5f),
                    text = word.pinyin ?: "N/A",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.CenterVertically),
                text = word.definition ?: "N/A",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "More Options"
            )
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Add to List") },
                    onClick = {
                        // Action to add the word to a list
                        menuExpanded = false
                    },
                    leadingIcon = { Icon(Icons.Filled.List, contentDescription = "Add to List") }
                )
                // Add more actions here if necessary, e.g., edit or remove
            }
        }
    }
}

