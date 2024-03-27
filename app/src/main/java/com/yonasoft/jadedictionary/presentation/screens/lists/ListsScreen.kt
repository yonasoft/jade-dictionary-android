@file:OptIn(
    ExperimentalMaterial3Api::class
)

package com.yonasoft.jadedictionary.presentation.screens.lists

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.data.constants.Screen
import com.yonasoft.jadedictionary.data.constants.SortOption
import com.yonasoft.jadedictionary.presentation.components.history_row.HistoryRow
import com.yonasoft.jadedictionary.presentation.components.search_bar.JadeSearchBar
import com.yonasoft.jadedictionary.presentation.components.word_list_row.WordListRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun ListsScreen(
    navController: NavController,
    viewModel: ListsViewModel = hiltViewModel(),
) {

    val isLoggedIn = viewModel.isLoggedIn.collectAsState()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val showBottomSheet = viewModel.showBottomSheet

    val query = viewModel.query
    val isActive = viewModel.isActive
    val wordLists = viewModel.wordLists.collectAsState()
    val history = viewModel.history.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Lists",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        if (!isLoggedIn.value) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Please log in to view your word lists.")
                Text("You can also sign in anonymously.")
                Button(onClick = { navController.navigate(Screen.Account.route) }) {
                    Text("Log In")
                }
            }
        } else {
            JadeSearchBar(
                modifier = Modifier.fillMaxWidth(),
                query = query,
                active = isActive,
                onSearch = {
                    viewModel.searchWordLists(it)
                    viewModel.addToHistory(it)
                    query.value = ""
                    isActive.value = false
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        showBottomSheet.value = true
                    }) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Add button",
                    )
                    Text(
                        text = "Sort: ${viewModel.currentSortMethod.value}",
                        textAlign = TextAlign.Center
                    )
                }
                OutlinedButton(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
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
            if (viewModel.isSyncing.value) {
                // Display a syncing indicator
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Syncing...", style = MaterialTheme.typography.bodySmall)
                }
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(
                    wordLists.value.size
                ) {
                    val wordList = wordLists.value[it]
                    WordListRow(
                        wordList = wordList,
                        onClick = {
                            Log.i("ids", "Id before nav ${wordList.firebaseId}")
                            navController.navigate(Screen.WordList.createRoute(wordList.firebaseId!!))
                        },
                        dropdownMenu = { menuExpanded, setMenuExpanded ->
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { setMenuExpanded(false) }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        viewModel.deleteWordList(
                                            context = context,
                                            wordList = wordList
                                        ); setMenuExpanded(false)
                                    },
                                    text = { Text("Remove List") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete List"
                                        )
                                    }
                                )
                                // Add more actions as needed
                            }
                        },
                    )
                    Divider(color = Color.Black)
                }
            }
            if (showBottomSheet.value) {
                SortBottomSheet(
                    sheetState = sheetState,
                    currentSortMethod = viewModel.currentSortMethod.value,
                    showBottomSheet = viewModel.showBottomSheet,
                    onSortSelected = { sortOption ->
                        viewModel.updateSortMethod(sortOption)
                    },
                    scope = scope
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun SortBottomSheet(
    sheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    currentSortMethod: SortOption,
    onSortSelected: (SortOption) -> Unit,
    scope: CoroutineScope
) {
    ModalBottomSheet(
        sheetState = sheetState,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
        onDismissRequest = { scope.launch { showBottomSheet.value = false } }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Sort by",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SortOption.entries.forEach { sortOption ->
                val isSelected = sortOption == currentSortMethod
                OutlinedButton(
                    onClick = {
                        onSortSelected(sortOption)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = MaterialTheme.shapes.small, // Use a less rounded shape for buttons
                    colors = ButtonDefaults.outlinedButtonColors( // Conditional color styling
                        contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = sortOption.toString(),
                        style = if (isSelected) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Button(onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet.value = false
                    }
                }
            }) {
                Text("Hide")
            }
        }
    }
}




