package com.yonasoft.jadedictionary.presentation.screens.lists

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.yonasoft.jadedictionary.data.datastore.StoreSearchHistory
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.data.respositories.WordListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ListsScreenViewModel @Inject constructor(
    private val wordListRepository: WordListRepository,
    private val storeSearchHistory: StoreSearchHistory,
) :
    ViewModel() {
    val query = mutableStateOf("")
    val isActive = mutableStateOf(false)
    val isExpanded = mutableStateOf(false)

    val addTitle = mutableStateOf("")
    val addDescription = mutableStateOf("")

    val wordDetail = mutableStateOf<WordList?>(null)

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history = _history.asStateFlow()

    private val _wordLists = MutableStateFlow<List<WordList>>(emptyList())
    val wordLists = _wordLists.asStateFlow()

    init {
        getAllWordLists()
        getHistory()
    }

    private fun getHistory() {
        viewModelScope.launch {
            // Assuming getSearchHistorySync() is adapted to be suspend and returns a List<String>
            _history.value = storeSearchHistory.getSearchHistorySync()
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

    private fun getAllWordLists() {
        viewModelScope.launch {
            wordListRepository.getAllWordLists().collect {
                _wordLists.value = it
            }
        }
    }

    fun addWordList(
        context: Context,
        title: String = addTitle.value,
        description: String? = addDescription.value
    ) {
        viewModelScope.launch {
            val currentDate = Date() // Gets the current date and time
            val wordList = WordList(
                title = title,
                description = description ?: "",
                createdAt = currentDate,
                lastUpdatedAt = currentDate,
                userUid = Firebase.auth.currentUser?.uid ?: "",
                wordIds = listOf() // Initialize with an empty list or whatever default you need
            )
            wordListRepository.insertWordList(wordList)
            Toast.makeText(context, "Word List: $title added", Toast.LENGTH_SHORT)
        }
    }

    fun deleteWordList(context: Context, wordList: WordList){
        viewModelScope.launch {
            wordListRepository.deleteWordList(wordList)
            Toast.makeText(context, "Word List: ${wordList.title} added", Toast.LENGTH_SHORT)
        }
    }
}