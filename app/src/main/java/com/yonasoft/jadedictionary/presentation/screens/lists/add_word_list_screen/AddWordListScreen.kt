package com.yonasoft.jadedictionary.presentation.screens.lists.add_word_list_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.presentation.screens.lists.ListsScreenViewModel

@Composable
fun AddWordListScreen(
    navController: NavController,
    viewModel: ListsScreenViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val title = viewModel.addTitle
    val description = viewModel.addDescription

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Add New Word List",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text(text = "Title") },
            value = title.value,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onValueChange = {
                title.value = it
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text(text = "Description(optional)") },
            minLines = 4,
            value = description.value,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onValueChange = {
                description.value = it
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    viewModel.addWordList(context)
                    navController.navigateUp()
                }
            ) {
                Text(text = "Add List")
            }
        }
    }
}