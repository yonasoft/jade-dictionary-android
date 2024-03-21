package com.yonasoft.jadedictionary.presentation.screens.practice.practice_word_select

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.presentation.components.modals.addToListModal.ListSelectionModal
import com.yonasoft.jadedictionary.presentation.components.word_row.WordRow
import com.yonasoft.jadedictionary.presentation.screens.practice.PracticeSharedViewModel

@ExperimentalMaterial3Api
@Composable
fun PracticeWordSelect(
    sharedViewModel: PracticeSharedViewModel = hiltViewModel(),
    onNext: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val words = sharedViewModel.practiceWords
    val isLoggedIn = sharedViewModel.isLoggedIn.collectAsState()
    val wordLists = sharedViewModel.wordLists.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val isWordDialogOpen = remember {
        mutableStateOf(false)
    }
    val showListSelectionModal = remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select Words to practice",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                OutlinedButton(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                    Text(text = "Search")
                }
                OutlinedButton(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = {
                        if (isLoggedIn.value) {
                            showListSelectionModal.value = true
                        } else {
                            Toast.makeText(context, "You need to be logged in to add from the list", Toast.LENGTH_LONG).show()
                        }
                    },
                    enabled = isLoggedIn.value,  // Disable button based on login state
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLoggedIn.value) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(imageVector = Icons.Default.List, contentDescription = "List Icon")
                    Text(text = "Add from list")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Words",
                style = MaterialTheme.typography.headlineSmall
            )
            if (words.value.size < 4) {
                Text(text = "Add at least 4 words!", color = MaterialTheme.colorScheme.error)
            }
            Divider(Modifier.padding(top = 8.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(
                    words.value.size
                ) {
                    val word = words.value[it]
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
                                        sharedViewModel.removeWordFromPractice(word)
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
        FloatingActionButton(
            onClick = {
                if (words.value.size >= 4) {
                    onNext()
                } else {
                    Toast.makeText(
                        context,
                        "Please select at least one quiz type",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(100.dp)
                .padding(16.dp),
            containerColor = if (words.value.size >= 4) MaterialTheme.colorScheme.secondary else Color.Gray,
        ) {
            Text("Next")
        }
    }
    if (showListSelectionModal.value) {
        ListSelectionModal(
            sheetState = sheetState,
            showBottomSheet = showListSelectionModal,
            scope = scope,
            wordLists = wordLists,
            onClick = {
                sharedViewModel.addFromWordList(it)
            })
    }
}