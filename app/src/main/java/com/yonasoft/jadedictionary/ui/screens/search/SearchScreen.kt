package com.yonasoft.jadedictionary.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.ui.components.historyrow.HistoryRow
import com.yonasoft.jadedictionary.ui.components.searchbar.JadeSearchBar


@Composable
fun SearchScreen(viewModel: SearchScreenViewModel = hiltViewModel()) {

    val query = viewModel.searchQuery
    val active = viewModel.active
    val history = viewModel.history.collectAsState()
    val searchResults = viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        JadeSearchBar(
            query = query.value,
            active = active.value,
            onSearch = {
                viewModel.onSearch(it)
                viewModel.addToHistory(it)
                query.value = ""
                active.value = false
            },
            changeQuery = {
                query.value = it
            },
            changeActive = {
                active.value = it
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

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                searchResults.value.size
            ) {
                val word = searchResults.value[it]
                Text(text = word.simplified.toString())
            }
        }
    }
}


