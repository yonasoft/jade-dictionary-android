package com.yonasoft.jadedictionary.presentation.screens.practice.practice_sessions.practice_modes.flash_card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.yonasoft.jadedictionary.data.constants.StringType
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.util.extractStringFromWord

@Composable
fun FlashCardPractice(
    word: Word,
    wordIndex: State<Int>,
    questionsType: State<StringType>,
    answerType: State<StringType>,
    timerRunning: State<Boolean>,
    onAnswer: (choice: String) -> Unit,
) {

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f),
        ) {
            FlipCard(
                front = extractStringFromWord(word, questionsType.value),
                back = extractStringFromWord(word, answerType.value!!),
            )
        }
        ChoiceSelector(
            timerRunning = timerRunning,
            onAnswer = onAnswer,
            wordIndex = wordIndex,
        )
    }
}


@Composable
fun FlipCard(front: String, back: String) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotationX = remember { Animatable(0f) }
    val rotationY = remember { Animatable(0f) }

    LaunchedEffect(isFlipped) {
        val targetRotationX = if (isFlipped) 180f else 0f
        rotationX.animateTo(
            targetValue = targetRotationX,
            animationSpec = TweenSpec(durationMillis = 500)
        )
    }

    val modifier = Modifier
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(onTap = { isFlipped = !isFlipped })
        }
        .graphicsLayer {
            // Apply the rotation around the X axis for a vertical flip
            this.rotationX = rotationX.value
            // This line ensures the back of the card is mirrored correctly
            scaleX = if (rotationX.value > 90f && rotationX.value <= 270f) -1f else 1f
            cameraDistance = 12f * density // Increase camera distance for more pronounced 3D effect
        }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        if (rotationX.value <= 90f || rotationX.value > 270f) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(8.dp),
            ) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = front, modifier = Modifier.padding(16.dp))
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(8.dp),
            ) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = back, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun ChoiceSelector(
    timerRunning: State<Boolean>,
    onAnswer: (choice: String) -> Unit,
    wordIndex: State<Int>
) {
    var selectedChoice by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(wordIndex.value) {
        selectedChoice = null
    }

    LaunchedEffect(timerRunning.value) {
        if (!timerRunning.value && selectedChoice == null) {
            selectedChoice = "Hard"
            onAnswer("Hard")
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        val choices = listOf("Hard", "Medium", "Easy")
        choices.forEach { choice ->
            val isSelected = choice == selectedChoice
            OutlinedButton(
                onClick = {
                    if (selectedChoice == null) { // Only allow selection if nothing has been selected
                        selectedChoice = choice
                        onAnswer(choice)
                    }
                },
                // Change the background and text color based on selection
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                enabled = selectedChoice == null || isSelected // Disable button if a choice is already made and it's not the selected one
            ) {
                when (choice) {
                    "Hard" -> Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hard Button",
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.error
                    )

                    "Medium" -> Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = "Medium Button",
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Yellow
                    )

                    "Easy" -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Easy Button",
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(text = choice)
            }
        }
    }
}


