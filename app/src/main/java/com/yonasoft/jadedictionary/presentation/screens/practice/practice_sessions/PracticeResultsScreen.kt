package com.yonasoft.jadedictionary.presentation.screens.practice.practice_sessions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.presentation.components.accordion.WordAccordion
import com.yonasoft.jadedictionary.presentation.screens.practice.PracticeSharedViewModel
import com.yonasoft.jadedictionary.util.Practice.mapIconFromAnswer
import kotlin.math.roundToInt

@ExperimentalMaterial3Api
@Composable
fun PracticeResultsScreen(
    navController: NavController,
    sharedViewModel: PracticeSharedViewModel = hiltViewModel()
) {


    val practiceMode = sharedViewModel.selectedModes.value.title

    val isStopwatch = sharedViewModel.isStopwatch
    val totalTime = sharedViewModel.stopwatchTime.longValue // Stopwatch time in milliseconds
    val numberOfWords = sharedViewModel.practiceWords.value.size // Number of words

    val totalSeconds = (totalTime / 1000).toDouble()
    val totalMinute = (totalSeconds / 60).toInt()
    val totalRemainingSeconds = (totalSeconds % 60).roundToInt()

    val avgSecondsPerWord = if (numberOfWords > 0) totalSeconds / numberOfWords else 0.0
    val avgMinutes = (avgSecondsPerWord / 60).toInt()
    val avgRemainingSeconds = (avgSecondsPerWord % 60).roundToInt()

    val answers = sharedViewModel.answers
    val wordList = sharedViewModel.wordLists.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "$practiceMode Results",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            )
            if(isStopwatch.value) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Total Time: ${totalMinute}m ${totalRemainingSeconds}s",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Average Time per Word: ${avgMinutes}m ${avgRemainingSeconds}s",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Words: $numberOfWords",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                )
                answers.value.map {
                    WordAccordion(
                        icon = mapIconFromAnswer(it.key),
                        title = it.key,
                        words = it.value,
                        wordLists = wordList,
                        onAddToWordList = { wordList, word ->
                            sharedViewModel.addToWordList(wordList, word)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Button(
                modifier = Modifier.padding(bottom = 12.dp),
                onClick = {
                    navController.navigateUp()
                }) {
                Text(text = "Exit")
            }
        }
    }
}