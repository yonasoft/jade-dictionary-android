package com.yonasoft.jadedictionary.presentation.screens.practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.presentation.screens.practice.practice_mode_selection.PracticeModeSettings
import com.yonasoft.jadedictionary.presentation.screens.practice.practice_word_select.PracticeWordSelect

@Composable
fun PracticeScreen(
    navController: NavController,
    sharedViewModel: PracticeSharedViewModel = hiltViewModel()
) {
    val screen = remember {
        mutableIntStateOf(0)
    }

    when (screen.value) {
        0 -> PracticeModeSettings(sharedViewModel = sharedViewModel,){
            screen.value += 1
        }
        1 -> PracticeWordSelect(sharedViewModel = sharedViewModel) {

        }
    }
}