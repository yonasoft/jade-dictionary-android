package com.yonasoft.jadedictionary.presentation.screens.search

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.data.datastore.StoreSearchHistory
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.data.respositories.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val storeSearchHistory: StoreSearchHistory,
) :
    ViewModel() {

    val isWordDialogOpen = mutableStateOf(false)
    private val _searchResults = MutableStateFlow<List<Word>>(emptyList())
    private val _history = MutableStateFlow<List<String>>(emptyList())
    val searchQuery = mutableStateOf("")
    val history = _history.asStateFlow()
    val active = mutableStateOf(false)
    val searchResults = _searchResults.asStateFlow()

    init {
        getHistory()
    }

    private fun getHistory() {
        viewModelScope.launch {
            // Assuming getSearchHistorySync() is adapted to be suspend and returns a List<String>
            _history.value = storeSearchHistory.getSearchHistorySync()
        }
    }

    fun addToHistory(query: String = searchQuery.value) {
        viewModelScope.launch {
            val newHistory = mutableListOf<String>()
            newHistory.addAll(history.value)
            newHistory.add(0, query)
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

    fun onSearch(query: String = searchQuery.value) {
        viewModelScope.launch {
            wordRepository.searchWord(query).collect { words ->
                _searchResults.value = words
                Log.i("onSearch", words.toString())
            }
        }
    }
}