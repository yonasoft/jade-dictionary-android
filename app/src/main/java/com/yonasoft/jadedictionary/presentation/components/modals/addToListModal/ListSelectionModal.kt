package com.yonasoft.jadedictionary.presentation.components.modals.addToListModal

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
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.presentation.components.word_list_row.WordListRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSelectionModal(
    sheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    scope: CoroutineScope,
    wordLists: State<List<WordList>>,
    onClick: (wordList:WordList) -> Unit,
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

            Button(
                modifier = Modifier.padding(vertical = 8.dp)
                ,onClick = {
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
                    wordLists.value.size
                ) {
                    val wordList = wordLists.value[it]
                    WordListRow(
                        wordList = wordList,
                        onClick = {
                            onClick(wordList)
                        },
                    )
                    Divider(color = Color.Black)
                }
            }
        }
    }
}