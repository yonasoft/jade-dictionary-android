package com.yonasoft.jadedictionary.presentation.components.accordion

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.presentation.components.modals.ListSelectionModal
import com.yonasoft.jadedictionary.presentation.components.word_row.WordRow

@ExperimentalMaterial3Api
@Composable
fun WordAccordion(
    icon: @Composable (() -> Unit)?,
    title: String,
    words: List<Word>,
    wordLists: State<List<WordList>>,
    onAddToWordList: (wordList: WordList, word: Word) -> Unit,
    onClick: (() -> Unit)? = null,
) {

    val user = rememberSaveable {
        mutableStateOf(Firebase.auth.currentUser)
    }
    val selectedWord = rememberSaveable {
        mutableStateOf<Word?>(null)
    }
    val isWordDialogOpen = rememberSaveable {
        mutableStateOf(false)
    }
    val expanded = rememberSaveable {
        mutableStateOf(false)
    }
    val showBottomSheet = rememberSaveable {
        mutableStateOf(false)
    }


    val transition = updateTransition(targetState = expanded.value, label = "transition")
    val iconRotateDeg by transition.animateFloat(label = "icon change") { state ->
        if (state) {
            0f
        } else {
            180f
        }
    }
    val color by transition.animateColor(label = "color change") { state ->
        if (state) {
            Color.Black.copy()
        } else {
            MaterialTheme.colorScheme.surface
        }
    }

    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable {
                    if (onClick != null) {
                        onClick()
                    } else {
                        expanded.value = !expanded.value
                    }
                },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                icon?.let { it() }
                Text(text = title)
            }
            if (expanded.value) {
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
                            dropdownMenu = if (user.value != null) { menuExpanded, setMenuExpanded ->
                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { setMenuExpanded(false) }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Add to List") },
                                        onClick = {
                                            selectedWord.value = word
                                            showBottomSheet.value = true
                                            setMenuExpanded(false)
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.List,
                                                contentDescription = "Add to List",
                                            )

                                        }
                                    )
                                }
                            } else null
                        )
                        Divider(color = Color.Black)
                        if (showBottomSheet.value) {
                            ListSelectionModal(
                                sheetState = rememberModalBottomSheetState(),
                                showBottomSheet = showBottomSheet,
                                scope = rememberCoroutineScope(),
                                wordLists = wordLists,
                                onClick = { wordList ->
                                    onAddToWordList(wordList, selectedWord.value!!)
                                    showBottomSheet.value = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}