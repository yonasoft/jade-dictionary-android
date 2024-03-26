package com.yonasoft.jadedictionary.presentation.screens.practice.practice_sessions.multi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yonasoft.jadedictionary.data.constants.StringType
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.util.extractStringFromWord

@Composable
fun MultipleChoicePractice(
    word: Word,
    words: State<List<Word>>,
    questionType: State<StringType>,
    answerType: State<StringType>,
    timerRunning: State<Boolean>? = null,
    onAnswer: (result: String) -> Unit,
) {

    val questionString = extractStringFromWord(word, questionType.value)
    val choices = rememberSaveable {
        mutableStateOf(randomizeChoices(word, words.value))
    }

    LaunchedEffect(word) {
        choices.value = randomizeChoices(word, words.value)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = questionString,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RadioChoices(
                        choices = choices.value,
                        answer = word,
                        answerType = answerType.value,
                        onAnswer = {
                            onAnswer(it)
                        },
                        timerRunning = timerRunning
                    )
                }
            }
        }
    }
}

@Composable
fun RadioChoices(
    choices: List<Word>,
    answer: Word,
    answerType: StringType,
    onAnswer: (String) -> Unit,
    timerRunning: State<Boolean>? = null,
) {
    val selectedChoice = rememberSaveable { mutableStateOf<Word?>(null) }
    val areRadiosEnabled = rememberSaveable {
        mutableStateOf(true)
    }

    LaunchedEffect(answer) {
        selectedChoice.value = null
        areRadiosEnabled.value = true
    }

    timerRunning?.let { timerState ->
        LaunchedEffect(timerState.value) {
            // Check if the timer is not running and no choice has been selected yet
            if (!timerState.value && selectedChoice == null) {
                areRadiosEnabled.value = true
                onAnswer("Incorrect")
            }
        }
    }

    Column {
        choices.forEach { choice ->
            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clickable {
                        if (areRadiosEnabled.value) {
                            selectedChoice.value = choice
                            val result =
                                if (selectedChoice.value!! == answer) "Correct" else "Incorrect"
                            areRadiosEnabled.value = false
                            onAnswer(result)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = choice == selectedChoice.value,
                    onClick = {
                        if (areRadiosEnabled.value) {
                            selectedChoice.value = choice
                            val result =
                                if (selectedChoice.value!! == answer) "Correct" else "Incorrect"
                            areRadiosEnabled.value = false
                            onAnswer(result)
                        }
                    },
                    enabled = areRadiosEnabled.value
                )
                Text(
                    text = extractStringFromWord(choice, answerType),
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            }
        }
    }
}

fun randomizeChoices(correctWord: Word, allWords: List<Word>): List<Word> {
    val filteredWords = allWords.filter { it != correctWord }
    val shuffledWords = filteredWords.shuffled()
    val choices = shuffledWords.take(3).toMutableList()
    choices.add(correctWord)
    return choices.shuffled()
}