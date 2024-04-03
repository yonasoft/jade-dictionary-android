package com.yonasoft.jadedictionary.presentation.screens.search

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.data.datastore.StoreSearchHistory
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.data.respositories.FirebaseAuthRepository
import com.yonasoft.jadedictionary.data.respositories.WordListRepository
import com.yonasoft.jadedictionary.data.respositories.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: FirebaseAuthRepository,
    private val wordRepository: WordRepository,
    private val wordListRepository: WordListRepository,
    private val storeSearchHistory: StoreSearchHistory,
) :
    ViewModel() {

    val isLoggedIn = mutableStateOf(false)
    val isWordDialogOpen = mutableStateOf(false)
    private val _searchResults = MutableStateFlow<List<Word>>(emptyList())
    private val _history = MutableStateFlow<List<String>>(emptyList())
    val searchQuery = mutableStateOf("")
    val history = _history.asStateFlow()
    val active = mutableStateOf(false)
    val searchResults = _searchResults.asStateFlow()

    private val _wordLists = MutableStateFlow<List<WordList>>(emptyList())
    val wordLists = _wordLists.asStateFlow()
    val selectedWord = mutableStateOf<Word?>(null)
    val showAddToListBottomSheet = mutableStateOf(false)

    init {
        getHistory()
        authRepository.getAuth().addAuthStateListener { auth ->
            viewModelScope.launch(Dispatchers.IO) {
                isLoggedIn.value = auth.currentUser != null
                if (auth.currentUser != null) {
                    getWordLists()
                    getHistory()
                } else {
                    _wordLists.value = emptyList()
                }
            }
        }
    }

    private fun getHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _history.value = storeSearchHistory.getSearchHistorySync()
        }
    }

    fun addToHistory(query: String = searchQuery.value) {
        viewModelScope.launch(Dispatchers.IO) {
            val newHistory = mutableListOf<String>()
            newHistory.addAll(history.value)
            newHistory.add(0, query)
            _history.value = newHistory
            storeSearchHistory.storeSearchHistory(newHistory)
        }
    }

    fun removeFromHistory(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val newHistory = mutableListOf<String>()
            newHistory.addAll(history.value)
            newHistory.removeAt(index)
            _history.value = newHistory
            storeSearchHistory.storeSearchHistory(newHistory)
        }
    }

    fun onSearch(query: String = searchQuery.value) {
        viewModelScope.launch(Dispatchers.IO) {
            wordRepository.searchWord(query).collect { words ->
                _searchResults.value = words
                Log.i("onSearch", words.toString())
            }
        }
    }

    private fun getWordLists() {
        if (authRepository.getAuth().currentUser == null) return
        viewModelScope.launch(Dispatchers.IO) {
            wordListRepository.getWordLists().collect {
                _wordLists.value = it
            }
        }
    }

    fun addToWordList(wordList: WordList, word: Word) {
        val wordIds = wordList.wordIds
        if (word.id in wordIds) {
            Toast.makeText(context, "Word already in list", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val wordId = word.id
            val newWordListIds = mutableListOf<Long>()
            newWordListIds.addAll(wordList.wordIds)
            newWordListIds.add(wordId!!)
            val newWordList = wordList.copy(wordIds = newWordListIds, lastUpdatedAt = Date())
            Log.i("wordlist", "$newWordList")
            wordListRepository.addOrUpdateWordList(newWordList)
            Toast.makeText(context, "${word.simplified} added to ${wordList.title}", Toast.LENGTH_SHORT).show()
        }
    }
}