package com.yonasoft.jadedictionary.presentation.screens.lists

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.data.datastore.StoreSearchHistory
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.data.respositories.WordListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsScreenViewModel @Inject constructor(
    private val wordListRepository: WordListRepository,
    private val storeSearchHistory: StoreSearchHistory,
) :
    ViewModel() {
    val query  = mutableStateOf("")
    val isActive = mutableStateOf(false)
    val isExpanded= mutableStateOf(false)

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history = _history.asStateFlow()

    private val _wordLists = MutableStateFlow<List<WordList>>(emptyList())
    val wordLists = _wordLists.asStateFlow()

    init{
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
}