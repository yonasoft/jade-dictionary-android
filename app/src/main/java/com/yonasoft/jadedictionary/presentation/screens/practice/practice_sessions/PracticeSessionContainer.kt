package com.yonasoft.jadedictionary.presentation.screens.practice.practice_sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.data.constants_and_sealed.PracticeMode
import com.yonasoft.jadedictionary.data.constants_and_sealed.TimerDuration
import com.yonasoft.jadedictionary.presentation.screens.practice.PracticeSharedViewModel
import com.yonasoft.jadedictionary.presentation.screens.practice.practice_sessions.flash_card.FlashCardPractice
import com.yonasoft.jadedictionary.presentation.screens.practice.practice_sessions.multi.MultipleChoicePractice

@Composable
fun PracticeSessionContainer(
    sharedViewModel: PracticeSharedViewModel = hiltViewModel(),
) {
    val screen = sharedViewModel.screen
    val practiceMode = sharedViewModel.selectedModes
    val wordIndex = sharedViewModel.wordIndex
    val practiceWords = sharedViewModel.practiceWords
    val isStopwatch = sharedViewModel.isStopwatch
    val stopwatchTime = sharedViewModel.stopwatchTime
    val timerTime = sharedViewModel.timerTime
    val timerDuration = sharedViewModel.timerDuration
    val timerRunning = sharedViewModel.timerRunning
    val canNext = sharedViewModel.canNext
    val answers = sharedViewModel.answers
    val questionType = sharedViewModel.questionType
    val answerType = sharedViewModel.answerType
    val word = practiceWords.value[wordIndex.intValue]
    val quizType = sharedViewModel.quizType

    LaunchedEffect(key1 = isStopwatch.value, key2 = timerDuration.value, key3 = quizType.value) {
        practiceWords.value = practiceWords.value.shuffled()
        if (isStopwatch.value) {
            sharedViewModel.startStopwatch()
        }
        if (timerDuration.value != TimerDuration.None) {
            sharedViewModel.startTimer()
        }
    }

    LaunchedEffect(wordIndex.value) {
        sharedViewModel.randomizeQA()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (timerDuration.value != TimerDuration.None) {
                        Row {
                            Text(text = "Timer: ", fontWeight = FontWeight.Bold)
                            TimerDisplay(timerTime = timerTime)
                        }
                    }
                    if (isStopwatch.value) {
                        Row {
                            Text(text = "Stopwatch: ", fontWeight = FontWeight.Bold)
                            StopwatchDisplay(stopwatchTime = stopwatchTime)
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        sharedViewModel.onExitSession()
                    }) {
                        Text(text = "Exit")
                    }
                    Button(onClick = {
                        if (canNext.value) {
                            wordIndex.intValue++
                            if (wordIndex.intValue < practiceWords.value.size) {
                                sharedViewModel.startStopwatch()
                                sharedViewModel.startTimer()
                            } else {
                                screen.intValue++
                            }
                        } },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canNext.value) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        ) {
                        Text(text = "Next")
                    }
                }
                Divider(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        ) {
                        Text(text = "Word: ", fontWeight = FontWeight.Bold)
                        Text(text = "${wordIndex.value + 1}/${practiceWords.value.size}")
                    }
                    when (practiceMode.value) {
                        PracticeMode.FlashCards -> FlashCardPractice(
                            word = word,
                            wordIndex = wordIndex,
                            questionsType = questionType,
                            answerType = answerType,
                            timerRunning = if (timerDuration.value != TimerDuration.None) timerRunning else null,
                            onAnswer = {
                                sharedViewModel.onAnswer(result = it, word = word)
                            },
                        )

                        PracticeMode.MultipleChoice -> MultipleChoicePractice(
                            word = word,
                            words = practiceWords,
                            questionType = questionType,
                            answerType = answerType,
                            timerRunning = if (timerDuration.value != TimerDuration.None) timerRunning else null,
                            onAnswer = {
                                sharedViewModel.onAnswer(result = it, word = word)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun StopwatchDisplay(stopwatchTime: State<Long>) {
    val timeSeconds = stopwatchTime.value / 1000
    val minutes = (timeSeconds % 3600) / 60
    val seconds = timeSeconds % 60
    val millis = stopwatchTime.value % 1000 / 10

    Text(text = String.format("%02d:%02d.%02d", minutes, seconds, millis))
}

@Composable
fun TimerDisplay(timerTime: State<Long>) {
    val timeSeconds = timerTime.value / 1000
    val minutes = (timeSeconds % 3600) / 60
    val seconds = timeSeconds % 60

    Text(text = String.format("%02d:%02d", minutes, seconds))
}
