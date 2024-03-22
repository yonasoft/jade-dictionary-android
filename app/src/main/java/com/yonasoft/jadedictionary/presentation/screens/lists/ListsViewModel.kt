package com.yonasoft.jadedictionary.presentation.screens.lists

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.data.constants.SortOption
import com.yonasoft.jadedictionary.data.datastore.StoreSearchHistory
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.data.respositories.FirebaseAuthRepository
import com.yonasoft.jadedictionary.data.respositories.WordListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(

    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val wordListRepository: WordListRepository,
    private val storeSearchHistory: StoreSearchHistory,
) :
    ViewModel() {
    val query = mutableStateOf("")

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    val isActive = mutableStateOf(false)
    val isSyncing = mutableStateOf(false)
    var showBottomSheet = mutableStateOf(false)

    val currentSortMethod = mutableStateOf(SortOption.DATE_RECENT)
    val addTitle = mutableStateOf("")
    val addDescription = mutableStateOf("")

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history = _history.asStateFlow()
    private val _wordLists = MutableStateFlow<List<WordList>>(emptyList())
    val wordLists = _wordLists.asStateFlow()


    init {
        getHistory()
        firebaseAuthRepository.getAuth().addAuthStateListener { auth ->
            viewModelScope.launch {
                _isLoggedIn.value = auth.currentUser != null
                if (_isLoggedIn.value) {
                    // User is logged in, perform actions that require authentication
                    getAllWordList()
                } else {
                    // User is not logged in, clear sensitive data or adjust UI accordingly
                    _wordLists.value = emptyList()
                    _history.value = emptyList()
                }
            }
        }
    }

    private fun getHistory() {
        viewModelScope.launch {
            _history.value = storeSearchHistory.getSearchHistorySync()
        }
    }

    fun addToHistory(searchQuery: String = query.value) {
        viewModelScope.launch {
            val newHistory = mutableListOf<String>()
            newHistory.addAll(history.value)
            newHistory.add(0, searchQuery)
            _history.value = newHistory
            storeSearchHistory.storeSearchHistory(newHistory)
        }
    }

    fun removeFromHistory(index: Int) {
        viewModelScope.launch {
            val newHistory = mutableListOf<String>()
            newHistory.addAll(history.value)
            newHistory.removeAt(index)
            _history.value = newHistory
            storeSearchHistory.storeSearchHistory(newHistory)
        }
    }

    private fun sortWordLists(lists: List<WordList>, sortMethod: SortOption): List<WordList> {
        return when (sortMethod) {
            SortOption.TITLE_ASC -> lists.sortedBy { it.title.lowercase() }
            SortOption.TITLE_DESC -> lists.sortedByDescending { it.title.lowercase() }
            SortOption.DATE_RECENT -> lists.sortedByDescending { it.lastUpdatedAt }
            SortOption.DATE_LEAST_RECENT -> lists.sortedBy { it.lastUpdatedAt }
        }
    }

    fun updateSortMethod(method: SortOption) {
        currentSortMethod.value = method
        viewModelScope.launch {
            val sortedLists = sortWordLists(_wordLists.value, method)
            _wordLists.value = sortedLists
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

    fun searchWordLists(searchQuery: String) {
        viewModelScope.launch {
            // Assuming getAllWordLists() can handle search queries or replace with appropriate search function
            val updatedLists = wordListRepository.searchWordLists(searchQuery).first()
            _wordLists.value = updatedLists
        }
    }

    fun addWordList(title: String = addTitle.value, description: String? = null) {
        if (_isLoggedIn.value) {
            viewModelScope.launch {
                val userUid = firebaseAuthRepository.getAuth().currentUser?.uid ?: return@launch
                val newWordList = WordList(
                    title = title,
                    description = description,
                    createdAt = Date(),
                    lastUpdatedAt = Date(),
                    userUid = userUid,
                    wordIds = emptyList()
                )
                wordListRepository.addOrUpdateWordList(newWordList)
            }
        } else {
            Log.d("ListsViewModel", "User must be logged in to add a word list.")
        }
    }


    fun deleteWordList(context: Context, wordList: WordList) {
        if (_isLoggedIn.value) {
            viewModelScope.launch {
                wordListRepository.deleteWordList(wordList.firebaseId)
                Toast.makeText(context, "Word List: ${wordList.title} removed", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Log.d("ListsViewModel", "User must be logged in to delete a word list.")
        }
    }
}