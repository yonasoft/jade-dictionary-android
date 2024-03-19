package com.yonasoft.jadedictionary.presentation.screens.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.data.constants.Screen
import com.yonasoft.jadedictionary.presentation.components.history_row.HistoryRow
import com.yonasoft.jadedictionary.presentation.components.search_bar.JadeSearchBar
import com.yonasoft.jadedictionary.presentation.components.word_list_row.WordListRow

@Composable
fun ListsScreen(navController: NavController, viewModel: ListsScreenViewModel = hiltViewModel()) {

    val context = LocalContext.current

    val query = viewModel.query
    val isActive = viewModel.isActive
    val isExpanded = viewModel.isExpanded
    val wordLists = viewModel.wordLists.collectAsState()
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
            OutlinedButton(
                onClick = {
                navController.navigate(Screen.AddList.route)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add button",
                )
                Text(text = "Add List")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                wordLists.value.size
            ) { it ->
                val wordList = wordLists.value[it]
                WordListRow(
                    wordList = wordList,
                    onClick = {},
                    onDelete = {
                        viewModel.deleteWordList(context = context, wordList = wordList)
                    },
                )
                Divider(color = Color.Black)
            }
        }
    }
}