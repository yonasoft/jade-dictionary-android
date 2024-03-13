package com.yonasoft.jadedictionary.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.ui.components.historyrow.HistoryRow
import com.yonasoft.jadedictionary.ui.components.searchbar.JadeSearchBar
import com.yonasoft.jadedictionary.ui.components.wordrow.WordRow


@Composable
fun SearchScreen(viewModel: SearchScreenViewModel = hiltViewModel()) {

    val query = viewModel.searchQuery
    val active = viewModel.active
    val history = viewModel.history.collectAsState()
    val searchResults = viewModel.searchResults.collectAsState()

    var isWordDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp)
    ) {
        JadeSearchBar(query = query.value, active = active.value, onSearch = {
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
            ) { it ->
                val word = searchResults.value[it]
                WordRow(
                    word = word,
                    showDialog = { isOpen -> isWordDialogOpen = isOpen },
                    isDialogOpen = isWordDialogOpen,
                )
                Divider(color = Color.Black)
            }
        }
    }
}


