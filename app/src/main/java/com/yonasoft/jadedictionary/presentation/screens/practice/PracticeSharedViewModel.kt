package com.yonasoft.jadedictionary.presentation.screens.practice

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.data.constants.PracticeMode
import com.yonasoft.jadedictionary.data.constants.QuizType
import com.yonasoft.jadedictionary.data.constants.TimerDuration
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.data.respositories.FirebaseAuthRepository
import com.yonasoft.jadedictionary.data.respositories.WordListRepository
import com.yonasoft.jadedictionary.data.respositories.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PracticeSharedViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wordRepository: WordRepository,
    private val wordListRepository: WordListRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
) : ViewModel() {

    val screen = mutableIntStateOf(0)
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    //Settings
    val isStopwatch = mutableStateOf(false)
    val timerDuration = mutableStateOf<TimerDuration>(TimerDuration.None)
    val selectedModes = mutableStateOf<PracticeMode>(PracticeMode.FlashCards)
    val quizType = mutableStateOf<MutableList<QuizType>>(mutableListOf(QuizType.HanziDefinition))

    //Word adding
    val wordSearchQuery = mutableStateOf("")
    val queryActive = mutableStateOf(false)
    val searchResults = mutableStateOf<List<Word>>(emptyList())
    private val _wordLists = MutableStateFlow<List<WordList>>(emptyList())
    val wordLists = _wordLists.asStateFlow()

    //Word adding + practice session
    val wordIds = mutableStateOf(setOf<Long>())
    val practiceWords = mutableStateOf<List<Word>>(emptyList())

    //Practice session
    private val isStopwatchRunning = mutableStateOf(true)
    val stopwatchTime = mutableLongStateOf(0L)
    val timerTime = mutableLongStateOf(TimerDuration.None.durationInMillis ?: 0L)
    val timerRunning = mutableStateOf(false)
    val wordIndex = mutableIntStateOf(0)
    val answers = mutableStateOf<HashMap<String, MutableList<Word>>>(hashMapOf())
    val canNext = mutableStateOf(false)
    val answerType = mutableStateOf(quizType.value[0].stringType1)
    val questionType = mutableStateOf(quizType.value[0].stringType2)


    init {
        firebaseAuthRepository.getAuth().addAuthStateListener { auth ->
            viewModelScope.launch {
                _isLoggedIn.value = auth.currentUser != null
                if (auth.currentUser != null) {
                    getAllWordList()
                } else {
                    _wordLists.value = emptyList()
                }
            }
        }
    }

    fun addFromWordList(wordList: WordList) {
        viewModelScope.launch {
            val newWordIds = mutableSetOf<Long>()
            val newPracticeWords = mutableListOf<Word>()

            newWordIds.addAll(wordIds.value)
            newPracticeWords.addAll(practiceWords.value)

            val wordListWordsIds = wordList.wordIds
            newWordIds.addAll(wordListWordsIds)
            newPracticeWords.addAll(fetchWordsFromListIds(wordListWordsIds))

            wordIds.value = newWordIds
            practiceWords.value = newPracticeWords
        }
    }

    fun addWord(word: Word) {
        if (word.id in wordIds.value) return
        viewModelScope.launch {
            val newWordIds = mutableSetOf<Long>()
            val newPracticeWords = mutableListOf<Word>()

            newWordIds.addAll(wordIds.value)
            newPracticeWords.addAll(practiceWords.value)

            newWordIds.add(word.id!!)
            newPracticeWords.add(word)

            wordIds.value = newWordIds
            practiceWords.value = newPracticeWords
        }
    }

    fun removeWordFromPractice(word: Word) {
        viewModelScope.launch {
            val newWordIds = mutableSetOf<Long>()
            val newPracticeWords = mutableListOf<Word>()

            newWordIds.addAll(wordIds.value)
            newPracticeWords.addAll(practiceWords.value)

            newWordIds.remove(word.id!!)
            newPracticeWords.remove(word)

            wordIds.value = newWordIds
            practiceWords.value = newPracticeWords
        }
    }

    private fun getAllWordList() {
        if (firebaseAuthRepository.getAuth().currentUser != null) {
            viewModelScope.launch {
                wordListRepository.getWordLists().collect { lists ->
                    _wordLists.value = lists
                }
            }
        }
    }

    private suspend fun fetchWordsFromListIds(words: List<Long>): List<Word> {
        val res = mutableListOf<Word>()
        words.forEach {
            if (it !in wordIds.value) {
                val word = wordRepository.fetchWordById(it)!!
                res.add(word)
            }
        }
        return res
    }

    fun onSearchWord(query: String = wordSearchQuery.value) {
        viewModelScope.launch {
            wordRepository.searchWord(query).collect { words ->
                searchResults.value = words
            }
        }
    }

    fun startStopwatch() {
        viewModelScope.launch {
            val startTime =
                System.currentTimeMillis() - stopwatchTime.longValue
            isStopwatchRunning.value = true
            while (isStopwatchRunning.value) {
                delay(100) // Update every 100 milliseconds
                stopwatchTime.longValue = System.currentTimeMillis() - startTime
            }
        }
    }

    fun startTimer() {
        val totalTime = timerDuration.value.durationInMillis ?: 0L
        if (totalTime == 0L) {
            canNext.value = true
            return
        }
        canNext.value = false
        timerRunning.value = true
        timerTime.longValue = totalTime
        viewModelScope.launch {
            var timeLeft = totalTime
            while (timeLeft > 0 && timerRunning.value) {
                delay(1000) // Decrease every second
                timeLeft -= 1000
                timerTime.longValue = timeLeft
            }
            timerRunning.value = false
            canNext.value = true
        }
    }

    private fun pauseTimer() {
        timerRunning.value = false
    }

    fun resetTimer() {
        pauseTimer()
        timerTime.longValue = timerDuration.value.durationInMillis ?: 0L
    }

    private fun pauseStopwatch() {
        isStopwatchRunning.value = false
    }

    fun resetStopwatch() {
        // Resets the stopwatch to 0 and stops updating
        pauseStopwatch() // First, make sure the stopwatch is paused
        stopwatchTime.longValue = 0L // Reset the stopwatch time to 0
    }

    fun onBackFromWordSelect() {
        viewModelScope.launch {
            practiceWords.value = mutableListOf()
            wordIds.value = mutableSetOf()
            screen.intValue -= 1
        }
    }

    fun onExitSession() {
        viewModelScope.launch {
            resetTimer()
            resetStopwatch()
            canNext.value = false
            wordIndex.intValue = 0
            answers.value = hashMapOf()
            questionType.value = quizType.value[0].stringType1
            answerType.value = quizType.value[0].stringType2
            screen.intValue -= 1
        }
    }


    fun randomizeQA() {
        viewModelScope.launch {
            val randomQuizType = quizType.value.random()
            val answerQuestion =
                listOf(randomQuizType.stringType1, randomQuizType.stringType2).shuffled()
            answerType.value = answerQuestion[0]
            questionType.value = answerQuestion[1]
            Log.i(
                "randomizeQA ",
                "types: ${quizType.value}, ques:${questionType.value} answer:${answerType.value}"
            )
        }
    }

    fun onAnswer(result: String, word: Word) {
        viewModelScope.launch {
            if (result !in answers.value) {
                answers.value[result] = mutableListOf()
            }
            answers.value[result]!!.add(word)
        }
        if (timerDuration.value != TimerDuration.None) pauseTimer()
        if (isStopwatch.value) pauseStopwatch()
        canNext.value = true
    }

    fun addToWordList(wordList: WordList, word: Word) {
        val wordIds = wordList.wordIds
        if (word.id in wordIds) {
            Toast.makeText(context, "Word already in list", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            val wordId = word.id
            val newWordListIds = mutableListOf<Long>()
            newWordListIds.addAll(wordList.wordIds)
            newWordListIds.add(wordId!!)
            val newWordList = wordList.copy(wordIds = newWordListIds, lastUpdatedAt = Date())
            Log.i("wordlist", "$newWordList")
            wordListRepository.addOrUpdateWordList(newWordList)
            Toast.makeText(
                context,
                "${word.simplified} added to ${wordList.title}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}