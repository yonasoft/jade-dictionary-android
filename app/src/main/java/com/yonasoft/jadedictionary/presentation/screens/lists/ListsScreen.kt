package com.yonasoft.jadedictionary.presentation.screens.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.presentation.components.history_row.HistoryRow
import com.yonasoft.jadedictionary.presentation.components.search_bar.JadeSearchBar

@Composable
fun ListsScreen(viewModel: ListsScreenViewModel = hiltViewModel()) {

    val query = viewModel.query
    val isActive = viewModel.isActive
    val isExpanded = viewModel.isExpanded
    val wordList = viewModel.wordLists.collectAsState()
    val history = viewModel.history.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        JadeSearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = query,
            active = isActive,
            onSearch = {
            },
            changeQuery = {
                query.value = it
            },
            changeActive = {
                isActive.value = it
            }
        ) {
            history.value.forEachIndexed { index, it ->
                HistoryRow(
                    index = index,
                    text = it,
                    onClick = {
                        query.value = it
                    },
                    onRemove = {
                        viewModel.removeFromHistory(
                            index
                        )
                    },
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = { /*TODO*/ }) {

            }
            OutlinedButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add button",
                )
                Text(text = "Add List")
            }
        }
    }
}