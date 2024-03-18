package com.yonasoft.jadedictionary.presentation.components.history_row

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryRow(
    index: Int,
    text: String,
    onClick: (text: String) -> Unit,
    onRemove: (index: Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
    ) {
        Icon(
            modifier = Modifier.padding(end = 10.dp),
            imageVector = Icons.Default.History,
            contentDescription = "History Icon"
        )
        Text(text = text, modifier = Modifier
            .weight(1f)
            .clickable {
                onClick(text)
            })
        Icon(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .clickable {
                    onRemove(index)
                },
            imageVector = Icons.Default.Close,
            contentDescription = "Close Icon"
        )


    }
}

