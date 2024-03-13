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

    var query = viewModel.searchQuery.value
    var active = viewModel.active.value
    val history = viewModel.history.collectAsState()

    val searchResults = viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        JadeSearchBar(
            query = query,
            active = active,
            onSearch = {
                viewModel.onSearch()
            },
            changeQuery = {
                query = it
            },
            changeActive = { active = it }
        ) {
            history.value.forEachIndexed { index, it ->
                HistoryRow(
                    index = index, text = it,
                    onClick = {
                        viewModel.searchQuery.value = it
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


