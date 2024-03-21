package com.yonasoft.jadedictionary.presentation.components.modals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.presentation.components.search_bar.JadeSearchBar
import com.yonasoft.jadedictionary.presentation.components.word_row.WordRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordSelectionModal(
    sheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    scope: CoroutineScope,
    query: MutableState<String>,
    active: MutableState<Boolean>,
    onSearch: (text: String) -> Unit,
    words: State<List<Word>>,
    onClick: (word: Word) -> Unit,
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
            JadeSearchBar(query = query, active = active, onSearch = {
                onSearch(it)
                query.value = ""
                active.value = false
            }, changeQuery = {
                query.value = it
            }, changeActive = {
                active.value = it
            }) {

            }
            Button(
                modifier = Modifier.padding(vertical = 8.dp), onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet.value = false
                        }
                    }
                }) {
                Text("Hide")
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(
                    words.value.size
                ) {
                    val word = words.value[it]
                    WordRow(
                        word = word,
                        onClick = {
                            onClick(word)
                        },
                        dropdownMenu = null,
                        isDialogOpen = null,
                    )
                    Divider(color = Color.Black)
                }
            }
        }
    }
}