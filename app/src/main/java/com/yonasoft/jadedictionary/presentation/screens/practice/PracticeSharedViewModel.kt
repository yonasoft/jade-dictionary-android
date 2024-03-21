package com.yonasoft.jadedictionary.presentation.screens.practice

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PracticeSharedViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val wordListRepository: WordListRepository,
    firebaseAuthRepository: FirebaseAuthRepository,
) : ViewModel() {

    val modes = listOf(PracticeMode.FlashCards, PracticeMode.MultipleChoice)

    val practiceMode = mutableStateOf<PracticeMode>(PracticeMode.FlashCards)
    val quizType = mutableStateOf<Set<QuizType>>(setOf(QuizType.HanziDefinition))
    val timer = mutableStateOf<TimerDuration>(TimerDuration.None)
    val stopwatch = mutableStateOf(false)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    val wordSearchQuery = mutableStateOf("")
    val queryActive = mutableStateOf(false)
    val searchResults = mutableStateOf<List<Word>>(emptyList())

    private val _wordLists = MutableStateFlow<List<WordList>>(emptyList())
    val wordLists = _wordLists.asStateFlow()

    private val wordIds = mutableStateOf(setOf<Long>())
    val practiceWords = mutableStateOf<List<Word>>(emptyList())

    init {
        firebaseAuthRepository.getAuth().addAuthStateListener { auth ->
            _isLoggedIn.value = auth.currentUser != null
            if (_isLoggedIn.value) {
                getAllWordList()
            } else {
                _wordLists.value = emptyList()
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
            newPracticeWords.addAll(fetchFromListWordIds(wordListWordsIds))

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
        if (_isLoggedIn.value) {
            viewModelScope.launch {
                wordListRepository.getWordLists().collect { lists ->
                    _wordLists.value = lists
                }
            }
        }
    }

    private suspend fun fetchFromListWordIds(words: List<Long>): List<Word> {
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
}