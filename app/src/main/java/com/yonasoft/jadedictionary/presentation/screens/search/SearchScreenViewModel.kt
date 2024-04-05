package com.yonasoft.jadedictionary.presentation.screens.search

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.data.constants_and_sealed.UiEvent
import com.yonasoft.jadedictionary.data.constants_and_sealed.emitUiEvent
import com.yonasoft.jadedictionary.data.datastore.StoreSearchHistory
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.data.respositories.FirebaseAuthRepository
import com.yonasoft.jadedictionary.data.respositories.WordListRepository
import com.yonasoft.jadedictionary.data.respositories.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val wordRepository: WordRepository,
    private val wordListRepository: WordListRepository,
    private val storeSearchHistory: StoreSearchHistory,
) :
    ViewModel() {
    // Auth
    val isLoggedIn = mutableStateOf(false)

    // Search bar
    val active = mutableStateOf(false)
    private val _history = MutableStateFlow<MutableList<String>>(mutableListOf())
    val history = _history.asStateFlow()
    val searchQuery = mutableStateOf("")
    private val _searchResults = MutableStateFlow<List<Word>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    // Individual Word
    val isWordDialogOpen = mutableStateOf(false)

    // Add to word list modal
    val selectedWord = mutableStateOf<Word?>(null)
    val showAddToListBottomSheet = mutableStateOf(false)
    private val _wordLists = MutableStateFlow<List<WordList>>(emptyList())
    val wordLists = _wordLists.asStateFlow()

    init {
        getHistory()

        authRepository.getAuth().addAuthStateListener { auth ->
            viewModelScope.launch(Dispatchers.IO) {
                isLoggedIn.value = (auth.currentUser != null)
                getWordLists()
            }
        }
    }

    private fun getHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val retrievedHistory = storeSearchHistory.getSearchHistorySync() as MutableList<String>
            withContext(Dispatchers.Main){
                _history.value = retrievedHistory
            }
        }
    }

    fun addToHistory(query: String = searchQuery.value) {
        viewModelScope.launch(Dispatchers.Main) {
            _history.value.add(0, query)
            withContext(Dispatchers.IO){
                storeSearchHistory.storeSearchHistory(_history.value)
            }
        }
    }

    fun removeFromHistory(index: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            _history.value.removeAt(index)
            withContext(Dispatchers.IO){
                storeSearchHistory.storeSearchHistory(_history.value)
            }
        }
    }


    fun onSearch(query: String = searchQuery.value) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val words = wordRepository.searchWord(query).first()
                withContext(Dispatchers.Main){
                    _searchResults.value = words
                }
            } catch (e: Exception) {
                Log.e("SearchScreenVM", "Search failed", e)
            }
        }
    }

    fun addToWordList(wordList: WordList, word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            val message = if (word.id !in wordList.wordIds) {
                val updatedWordIds = wordList.wordIds.toMutableList().apply { add(word.id!!) }
                wordListRepository.addOrUpdateWordList(wordList.copy(wordIds = updatedWordIds, lastUpdatedAt = Date()))
                "${word.simplified} added to ${wordList.title}"
            } else {
                "Word already in list"
            }
            emitUiEvent(UiEvent.ShowToast(message))
        }
    }

    private suspend fun getWordLists() {
            if (isLoggedIn.value) {
                try {
                    val wordlist = wordListRepository.getWordLists().first()
                    withContext(Dispatchers.Main){
                        _wordLists.value = wordlist
                    }
                } catch (e: Exception) {
                    Log.e("SearchScreenVM", "Failed to fetch word lists", e)
                    _wordLists.value = emptyList()
                }
            }
        }
}