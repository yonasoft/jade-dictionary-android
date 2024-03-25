package com.yonasoft.jadedictionary.presentation.screens.practice.practice_sessions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.presentation.screens.practice.PracticeSharedViewModel
import kotlin.math.roundToInt

@Composable
fun PracticeResultsScreen(
    navController: NavController,
    sharedViewModel: PracticeSharedViewModel = hiltViewModel()
) {

    val practiceMode = sharedViewModel.practiceMode.value.title

    val totalTime = sharedViewModel.stopwatchTime.value // Stopwatch time in milliseconds
    val numberOfWords = sharedViewModel.practiceWords.value.size // Number of words

    val totalSeconds = (totalTime / 1000).toDouble()
    val totalMinute = (totalSeconds / 60).toInt()
    val totalRemainingSeconds = (totalMinute % 60)

    val avgSecondsPerWord = if (numberOfWords > 0) totalSeconds / numberOfWords else 0.0
    val avgMinutes = (avgSecondsPerWord / 60).toInt()
    val avgRemainingSeconds = (avgSecondsPerWord % 60).roundToInt()

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
            }
            Button(onClick = {
                navController.navigateUp()
            }) {
                Text(text = "Exit")
            }
        }
    }
}