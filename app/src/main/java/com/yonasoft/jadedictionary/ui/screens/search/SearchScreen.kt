package com.yonasoft.jadedictionary.ui.screens.search

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.ui.components.historyrow.HistoryRow


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchScreenViewModel = hiltViewModel()) {

    val searchResults = viewModel.searchResults.collectAsState()
    val history = viewModel.history.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(modifier = Modifier.fillMaxWidth(),
            query = viewModel.searchQuery.value,
            onQueryChange = { viewModel.searchQuery.value = it },
            onSearch = {
                viewModel.addToHistory(it)
                viewModel.active.value = false
                viewModel.onSearch(it)
            },
            active = viewModel.active.value,
            onActiveChange = {
                viewModel.active.value = it
            },
            placeholder = {
                Text(text = "Search by English, Hanzi or Pinyin...")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        if (viewModel.searchQuery.value.isEmpty()) {
                            viewModel.active.value = false
                        }
                        viewModel.searchQuery.value = ""
                    }, imageVector = Icons.Default.Close, contentDescription = "Close Icon"
                )

            }) {
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
                val word = viewModel.searchResults.value[it]
                Text(text = word.simplified.toString())
            }
        }
    }
}


