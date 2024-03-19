package com.yonasoft.jadedictionary.presentation.components.word_list_row

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.data.models.WordList

@Composable
fun WordListRow(wordList: WordList, onClick: () -> Unit, onDelete: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
            .padding(horizontal = 4.dp)
        // Make the entire row clickable
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
                .clickable(onClick = onClick), // Add padding to prevent text from overlapping with the icon button
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = wordList.title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = wordList.description ?: "",
                fontSize = 16.sp,
                maxLines = 1, // Prevents description from taking too much space
                overflow = TextOverflow.Ellipsis // Adds ellipsis for overflow
            )
        }
        // Action Button for more options
        IconButton(onClick = { menuExpanded = true }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options")
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    onClick = { onDelete(); menuExpanded = false },
                    text = { Text("Remove List") },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "Delete List") }
                )
                // Add more actions as needed
            }
        }
    }
}
