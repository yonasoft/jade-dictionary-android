package com.yonasoft.jadedictionary.presentation.screens.practice.practice_sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.data.constants.PracticeMode
import com.yonasoft.jadedictionary.data.constants.TimerDuration
import com.yonasoft.jadedictionary.presentation.screens.practice.PracticeSharedViewModel
import com.yonasoft.jadedictionary.presentation.screens.practice.practice_sessions.practice_modes.flash_card.FlashCardPractice
import com.yonasoft.jadedictionary.presentation.screens.practice.practice_sessions.practice_modes.multi.MultipleChoicePractice

@Composable
fun PracticeSessionContainer(
    sharedViewModel: PracticeSharedViewModel = hiltViewModel(),
) {
    val screen = sharedViewModel.screen

    val wordIndex = sharedViewModel.wordIndex
    val practiceWords = sharedViewModel.practiceWords
    val isStopwatch = sharedViewModel.isStopwatch
    val stopwatchTime = sharedViewModel.stopwatchTime
    val timerTime = sharedViewModel.timerTime
    val timerDuration = sharedViewModel.timerDuration
    val timerRunning = sharedViewModel.timerRunning
    val canNext = sharedViewModel.canNext
    val word = sharedViewModel.practiceWords.value[sharedViewModel.wordIndex.value]

    LaunchedEffect(key1 = isStopwatch.value, key2 = timerDuration.value) {
        if (isStopwatch.value) {
            sharedViewModel.startStopwatch()
        }
        if (timerDuration.value != TimerDuration.None) {
            sharedViewModel.startTimer()
        }
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

                    if (isStopwatch.value) {
                        Row {
                            Text(text = "Stopwatch: ", fontWeight = FontWeight.Bold)
                            StopwatchDisplay(stopwatchTime = stopwatchTime)
                        }
                    }
                }
                if (timerDuration.value != TimerDuration.None) {
                    Row {
                        Text(text = "Timer: ", fontWeight = FontWeight.Bold)
                        TimerDisplay(timerTime = timerTime)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(onClick = {
                        sharedViewModel.resetTimer()
                        sharedViewModel.resetStopwatch()
                        screen.value -= 1
                    }) {
                        Text(text = "Exit")
                    }
                    Button(onClick = {
                        sharedViewModel.pauseStopwatch()
                        sharedViewModel.pauseTimer()
                        canNext.value = false
                    }) {
                        Text(text = "Pause")
                    }
                }
                Divider(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                    when (sharedViewModel.practiceMode.value) {
                        PracticeMode.FlashCards -> FlashCardPractice()
                        PracticeMode.MultipleChoice -> MultipleChoicePractice()
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    if (canNext.value) {
                        wordIndex.value++
                        if(wordIndex.value < practiceWords.value.size) {
                            sharedViewModel.startStopwatch()
                            sharedViewModel.startTimer()
                        } else{
                            screen.value++
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(100.dp)
                    .padding(16.dp),
                containerColor = if (canNext.value) MaterialTheme.colorScheme.secondary else Color.Gray,
            ) {
                Text("Next")
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
