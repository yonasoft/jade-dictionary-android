@file:OptIn(
    ExperimentalMaterial3Api::class
)

package com.yonasoft.jadedictionary.presentation.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.presentation.components.history_row.HistoryRow
import com.yonasoft.jadedictionary.presentation.components.modals.addToListModal.AddToListModal
import com.yonasoft.jadedictionary.presentation.components.search_bar.JadeSearchBar
import com.yonasoft.jadedictionary.presentation.components.word_row.WordRow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchScreenViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val query = viewModel.searchQuery
    val active = viewModel.active
    val history = viewModel.history.collectAsState()
    val searchResults = viewModel.searchResults.collectAsState()
    val wordLists = viewModel.wordLists.collectAsState()

    val isWordDialogOpen = viewModel.isWordDialogOpen
    val showAddToListBottomSheet = viewModel.showAddToListBottomSheet
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp)
    ) {
        JadeSearchBar(query = query, active = active, onSearch = {
            viewModel.onSearch(it)
            viewModel.addToHistory(it)
            query.value = ""
            active.value = false
        }, changeQuery = {
            query.value = it
        }, changeActive = {
            active.value = it
        }) {
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
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                searchResults.value.size
            ) {
                val word = searchResults.value[it]
                WordRow(
                    word = word,
                    onClick = {
                        isWordDialogOpen.value = true
                    },
                    isDialogOpen = isWordDialogOpen,
                    dropdownMenu = { menuExpanded ->
                        DropdownMenu(
                            expanded = menuExpanded.value,
                            onDismissRequest = { menuExpanded.value = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Add to List") },
                                onClick = {
                                    viewModel.selectedWord.value = word
                                    showAddToListBottomSheet.value = true
                                    menuExpanded.value = false
                                },
                                leadingIcon = {

                                    Icon(
                                        imageVector = Icons.Filled.List,
                                        contentDescription = "Add to List",
                                    )

                                }
                            )
                        }
                    }
                )
                Divider(color = Color.Black)
            }
        }
    }
    if (showAddToListBottomSheet.value) {
        AddToListModal(
            sheetState = sheetState,
            showBottomSheet = showAddToListBottomSheet,
            scope = scope,
            wordLists = wordLists,
            onClick = { wordList ->
                viewModel.addToWordList(context, wordList, viewModel.selectedWord.value!!)
                showAddToListBottomSheet.value = false
            }
        )
    }
}




