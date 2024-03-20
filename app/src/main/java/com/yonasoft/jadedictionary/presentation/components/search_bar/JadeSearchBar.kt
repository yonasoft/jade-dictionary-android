package com.yonasoft.jadedictionary.presentation.components.search_bar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadeSearchBar(
    modifier: Modifier = Modifier.fillMaxWidth(),
    query: MutableState<String>,
    active: MutableState<Boolean>,
    onSearch: (query: String) -> Unit,
    changeQuery: (query: String) -> Unit,
    changeActive: (iSActive: Boolean) -> Unit,

    content: @Composable() (ColumnScope.() -> Unit),
) {
    SearchBar(
        modifier = modifier,
        query = query.value,
        onQueryChange = {
            changeQuery(it)
        },
        onSearch = {
            onSearch(it)
        },
        active = active.value,
        onActiveChange = {
            changeActive(it)
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
                    changeQuery("")
                    if(active.value) {
                        if (query.value.isEmpty()) {
                            changeActive(false)
                        }
                    } else{
                        onSearch("")
                    }
                }, imageVector = Icons.Default.Close, contentDescription = "Close Icon"
            )

        },
    ) {
        content()
    }
}