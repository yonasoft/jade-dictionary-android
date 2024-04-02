package com.yonasoft.jadedictionary.presentation.screens.practice.practice_mode_selection

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.data.constants.PracticeMode
import com.yonasoft.jadedictionary.data.constants.QuizType
import com.yonasoft.jadedictionary.data.constants.TimerDuration
import com.yonasoft.jadedictionary.presentation.screens.practice.PracticeSharedViewModel

@Composable
fun PracticeModeSettings(
    sharedViewModel: PracticeSharedViewModel = hiltViewModel(),
    onNext: () -> Unit
) {

    val context = LocalContext.current
    val modes = listOf(PracticeMode.FlashCards, PracticeMode.MultipleChoice)
    val isQuizTypeSelected = sharedViewModel.quizType.value.isNotEmpty()

    LaunchedEffect(sharedViewModel.quizType.value){
        Log.i("quiz type", sharedViewModel.quizType.value.toString())
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Select Practice Mode",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                modes.forEach { mode ->
                    PracticeModeCard(
                        practiceMode = mode,
                        isSelected = sharedViewModel.selectedModes.value == mode,
                        onSelected = { selectedMode ->
                            sharedViewModel.selectedModes.value = selectedMode
                        }
                    )
                }

                Text("Select Quiz Type", modifier = Modifier.padding(vertical = 8.dp))
                if (sharedViewModel.quizType.value.isEmpty()) {
                    Text(
                        "Please select at least one quiz type.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                QuizTypeSelector(sharedViewModel = sharedViewModel)

                Text("Timer Duration", modifier = Modifier.padding(vertical = 8.dp))
                TimerDropdownSelector(sharedViewModel = sharedViewModel)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Stopwatch", modifier = Modifier.padding(vertical = 8.dp))
                    StopwatchToggle(sharedViewModel = sharedViewModel)
                }
            }
            FloatingActionButton(
                onClick = {
                    if (isQuizTypeSelected) {
                        onNext()
                    } else {
                        Toast.makeText(
                            context,
                            "Please select at least one quiz type",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(100.dp)
                    .padding(16.dp),
                containerColor = if (isQuizTypeSelected) MaterialTheme.colorScheme.secondary else Color.Gray,
            ) {
                Text("Next")
            }
        }
    }
}


@Composable
fun PracticeModeCard(
    practiceMode: PracticeMode,
    isSelected: Boolean,
    onSelected: (PracticeMode) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onSelected(practiceMode) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = practiceMode.icon),
                    contentDescription = "${practiceMode.title} icon",
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = practiceMode.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = practiceMode.description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


@Composable
fun QuizTypeSelector(sharedViewModel: PracticeSharedViewModel) {
    val quizTypes = listOf(QuizType.HanziDefinition, QuizType.HanziPinyin)
    Row(
        modifier = Modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            quizTypes.forEach { quizType ->
                val isSelected = sharedViewModel.quizType.value.contains(quizType)
                Chip(
                    label = "${quizType.stringType1.name} <-> ${quizType.stringType2.name}",
                    isSelected = isSelected,
                    onSelectionChanged = { selected ->
                        val currentSelection = sharedViewModel.quizType.value.map { it }.toMutableList()
                        if (selected) currentSelection.add(quizType)
                        else currentSelection.remove(quizType)
                        sharedViewModel.quizType.value = currentSelection
                    },
                )
            }
        }
    }
}

@Composable
fun Chip(
    label: String,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clickable { onSelectionChanged(!isSelected) }
            .clip(RoundedCornerShape(50)) // Ensures the background and border are rounded
            .then(
                if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                else Modifier
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
                        RoundedCornerShape(50)
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TimerDropdownSelector(sharedViewModel: PracticeSharedViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val timerDurations = listOf(
        TimerDuration.None,
        TimerDuration.ThreeSeconds,
        TimerDuration.FiveSeconds,
        TimerDuration.TenSeconds,
        TimerDuration.FifteenSeconds,
        TimerDuration.ThirtySeconds,
        TimerDuration.OneMinute,
    )
    val selectedDuration = sharedViewModel.timerDuration.value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = selectedDuration.time,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            timerDurations.forEach { duration ->
                DropdownMenuItem(
                    text = { Text(duration.time) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        sharedViewModel.timerDuration.value = duration
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun StopwatchToggle(sharedViewModel: PracticeSharedViewModel) {
    Row(modifier = Modifier.padding(16.dp)) {
        Switch(
            checked = sharedViewModel.isStopwatch.value,
            onCheckedChange = { sharedViewModel.isStopwatch.value = it }
        )
    }
}

