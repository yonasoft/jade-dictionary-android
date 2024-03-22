package com.yonasoft.jadedictionary.presentation.screens.practice

import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PracticeSharedViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val wordListRepository: WordListRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
) : ViewModel() {

    val screen = mutableStateOf(0)
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    //Settings
    val modes = listOf(PracticeMode.FlashCards, PracticeMode.MultipleChoice)
    val isStopwatch = mutableStateOf(false)
    val timerDuration = mutableStateOf<TimerDuration>(TimerDuration.None)
    val practiceMode = mutableStateOf<PracticeMode>(PracticeMode.FlashCards)
    val quizType = mutableStateOf<Set<QuizType>>(setOf(QuizType.HanziDefinition))

    //Word adding
    val wordSearchQuery = mutableStateOf("")
    val queryActive = mutableStateOf(false)
    val searchResults = mutableStateOf<List<Word>>(emptyList())
    private val _wordLists = MutableStateFlow<List<WordList>>(emptyList())
    val wordLists = _wordLists.asStateFlow()

    //Word adding + practice session
    private val wordIds = mutableStateOf(setOf<Long>())
    val practiceWords = mutableStateOf<List<Word>>(emptyList())

    //Practice session
    val isStopwatchRunning = mutableStateOf(true)
    val stopwatchTime = mutableStateOf(0L)
    val timerTime = mutableStateOf(TimerDuration.None.durationInMillis ?: 0L)
    val timerRunning = mutableStateOf(false)
    val wordIndex = mutableIntStateOf(0)
    val answers = hashMapOf<String,MutableList<Word>>()
    val canNext = mutableStateOf(false)

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
        if (firebaseAuthRepository.getAuth().currentUser!=null) {
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
            val startTime = System.currentTimeMillis() - stopwatchTime.value // Adjust to continue from current time
            isStopwatchRunning.value = true
            while (isStopwatchRunning.value) {
                delay(100) // Update every 100 milliseconds
                stopwatchTime.value = System.currentTimeMillis() - startTime
            }
        }
    }

    fun startTimer() {
        val totalTime = timerDuration.value.durationInMillis ?: 0L
        if (totalTime == 0L) {
            // If there's no timer duration, allow proceeding immediately.
            canNext.value = true
            return
        }
        canNext.value = false // Ensure "Next" is disabled when the timer starts.
        timerRunning.value = true
        timerTime.value = totalTime
        viewModelScope.launch {
            var timeLeft = totalTime
            while (timeLeft > 0 && timerRunning.value) {
                delay(1000) // Decrease every second
                timeLeft -= 1000
                timerTime.value = timeLeft
            }
            timerRunning.value = false
            canNext.value = true
        }
    }

    fun pauseTimer() {
        // Stops the timer without resetting the time left
        timerRunning.value = false
    }

    fun resetTimer() {
        // Resets the timer to the original duration
        pauseTimer()
        timerTime.value = timerDuration.value.durationInMillis ?: 0L
    }

    fun pauseStopwatch() {
        // To pause the stopwatch, we need to stop updating its time
        // This can be tricky because your current implementation continuously updates
        // Consider introducing a variable to control whether the stopwatch should update
        isStopwatchRunning.value = false // You'll need to add this state variable
    }

    fun resetStopwatch() {
        // Resets the stopwatch to 0 and stops updating
        pauseStopwatch() // First, make sure the stopwatch is paused
        stopwatchTime.value = 0L // Reset the stopwatch time to 0
    }

}