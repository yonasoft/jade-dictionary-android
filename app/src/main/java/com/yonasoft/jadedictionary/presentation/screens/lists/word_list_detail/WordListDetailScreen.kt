package com.yonasoft.jadedictionary.presentation.screens.lists.word_list_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.presentation.components.word_row.WordRow


@Composable
fun WordListDetailScreen(
    navController: NavController,
    wordListId:String,
    viewModel: WordListDetailViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = wordListId){
        viewModel.initiateWordDetails(wordListId)
    }

    val words by viewModel.wordListWords.collectAsState()
    val isWordDialogOpen = remember {
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = viewModel.editTitle.value,
            onValueChange = { viewModel.editTitle.value = it },
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = viewModel.editDescription.value,
            onValueChange = { viewModel.editDescription.value = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        Button(
            modifier = Modifier.padding(vertical = 8.dp),
            onClick = {
                viewModel.saveWordList()
                navController.navigateUp()
            },
        ) {
            Text("Save Changes")
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                words.size
            ) {
                val word = words[it]
                WordRow(
                    word = word,
                    onClick = {
                        isWordDialogOpen.value = true
                    },
                    isDialogOpen = isWordDialogOpen,
                    dropdownMenu = { menuExpanded, setMenuExpanded ->
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { setMenuExpanded(false) }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Remove") },
                                onClick = {
                                    viewModel.removeWord(word)
                                    setMenuExpanded(false)
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Remove from from List"
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
}


