package com.yonasoft.jadedictionary.presentation.screens.practice.practice_word_select

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.presentation.screens.practice.PracticeSharedViewModel

@Composable
fun PracticeWordSelect(
    sharedViewModel: PracticeSharedViewModel = hiltViewModel(),
    onNext: () -> Unit
) {
    val words = sharedViewModel.practiceWords

    Column(Modifier.fillMaxSize()) {

    }
}