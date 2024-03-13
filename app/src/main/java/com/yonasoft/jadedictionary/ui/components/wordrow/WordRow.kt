package com.yonasoft.jadedictionary.ui.components.wordrow

import WordDetailDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yonasoft.jadedictionary.data.models.Word


@Composable
fun WordRow(word: Word, showDialog: (Boolean) -> Unit, isDialogOpen: Boolean) {

    var menuExpanded by remember { mutableStateOf(false) }

    if (isDialogOpen) {
        WordDetailDialog(word = word, onDismiss = { showDialog(false) })
    }


    if (isDialogOpen) {
        WordDetailDialog(word = word, onDismiss = { showDialog(false) })
    }

    val traditionalText =
        if (word.newTraditional == word.simplified) "" else "(${word.traditional})"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
            .height(90.dp)
    ) {
        Row(modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .align(Alignment.CenterVertically)
            .clickable { showDialog(true) }) {
            Text(
                modifier = Modifier
                    .weight(.6f)
                    .align(Alignment.CenterVertically),
                text = word.simplified + traditionalText,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier
                    .weight(.5f)
                    .align(Alignment.CenterVertically),
                text = word.pinyin ?: ("N/A"),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                text = word.definition ?: ("N/A"),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(
            onClick = {
                menuExpanded = true
            }, modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Menu Icon",
            )
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Add to List") },
                    onClick = {
                        // Handle add to list here
                        menuExpanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Add to List"
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Add to Favorites") },
                    onClick = {
                        // Handle add to favorites here
                        menuExpanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Add to Favorites"
                        )
                    }
                )
            }
        }
    }
}
