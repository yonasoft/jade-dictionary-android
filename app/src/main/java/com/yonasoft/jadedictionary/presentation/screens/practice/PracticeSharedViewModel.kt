package com.yonasoft.jadedictionary.presentation.screens.practice

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.yonasoft.jadedictionary.data.constants.PracticeMode
import com.yonasoft.jadedictionary.data.constants.QuizType
import com.yonasoft.jadedictionary.data.constants.TimerDuration
import com.yonasoft.jadedictionary.data.models.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PracticeSharedViewModel @Inject constructor():ViewModel() {

    val modes= listOf(PracticeMode.FlashCards, PracticeMode.MultipleChoice)

    val practiceMode = mutableStateOf<PracticeMode>(PracticeMode.FlashCards)
    val quizType = mutableStateOf<Set<QuizType>>(setOf(QuizType.HanziDefinition))
    val timer = mutableStateOf<TimerDuration>(TimerDuration.None)
    val stopwatch = mutableStateOf(false)
    val practiceWords = mutableStateOf<MutableList<Word>>(mutableListOf())


}