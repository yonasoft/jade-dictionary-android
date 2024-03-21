package com.yonasoft.jadedictionary.presentation.screens.practice

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.presentation.screens.practice.practice_mode_selection.PracticeModeSettings
import com.yonasoft.jadedictionary.presentation.screens.practice.practice_word_select.PracticeWordSelect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    navController: NavController,
    sharedViewModel: PracticeSharedViewModel = hiltViewModel()
) {
    val screen = remember {
        mutableIntStateOf(0)
    }

    when (screen.intValue) {
        0 -> PracticeModeSettings(sharedViewModel = sharedViewModel){
            screen.intValue += 1
        }
        1 -> PracticeWordSelect(sharedViewModel = sharedViewModel) {

        }
    }
}