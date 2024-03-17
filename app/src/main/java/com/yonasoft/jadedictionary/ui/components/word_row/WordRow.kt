package com.yonasoft.jadedictionary.ui.components.word_row

import com.yonasoft.jadedictionary.ui.components.dialogs.word_detail_dialog.WordDetailDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
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
        if (word.traditional == word.simplified) "" else "(${word.traditional})"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
            .padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 100.dp)
                .clickable { showDialog(true) }, verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(.5f),
                    text = word.simplified + traditionalText,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(.5f),
                    text = word.pinyin ?: ("N/A"),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically),
                text = word.definition ?: ("N/A"),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
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
            }
        }
    }
}
