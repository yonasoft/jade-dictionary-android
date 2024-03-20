package com.yonasoft.jadedictionary.presentation.screens.lists

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.yonasoft.jadedictionary.data.datastore.StoreSearchHistory
import com.yonasoft.jadedictionary.data.enums.SortOption
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.data.respositories.WordListRepository
import com.yonasoft.jadedictionary.data.respositories.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val wordListRepository: WordListRepository,
    private val storeSearchHistory: StoreSearchHistory,
) :
    ViewModel() {
    val query = mutableStateOf("")
    val isActive = mutableStateOf(false)

    var showBottomSheet = mutableStateOf(false)
    val currentSortMethod = mutableStateOf(SortOption.DATE_RECENT)
    val addTitle = mutableStateOf("")
    val addDescription = mutableStateOf("")
    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history = _history.asStateFlow()
    private val _wordLists = MutableStateFlow<List<WordList>>(emptyList())
    val wordLists = _wordLists.asStateFlow()

    val editTitle = mutableStateOf("")
    val editDescription = mutableStateOf("")
    private val wordListDetail = mutableStateOf<WordList?>(null)
    private val _wordListWords = MutableStateFlow<List<Word>>(emptyList())
    val wordListWords = _wordListWords.asStateFlow()

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

    private fun getAllWordLists() {
        viewModelScope.launch {
            wordListRepository.getAllWordLists().collect {
                _wordLists.value = it
            }
        }
    }
    fun searchWordList(searchQuery:String = query.value){
        viewModelScope.launch {
            wordListRepository.searchWordList(searchQuery).collect{
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
            Toast.makeText(context, "Word List: $title added", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteWordList(context: Context, wordList: WordList) {
        viewModelScope.launch {
            wordListRepository.deleteWordList(wordList)
            Toast.makeText(context, "Word List: ${wordList.title} added", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveWordList() {
        viewModelScope.launch {
            val newWordList = wordListDetail.value!!.copy(
                title = editTitle.value,
                description = editDescription.value,
                lastUpdatedAt = Date(),
            )
            wordListRepository.updateWordList(newWordList)
        }
    }

    fun removeWord(word: Word) {
        viewModelScope.launch {
            val newWordIds = wordListDetail.value?.wordIds?.toMutableList() ?: mutableListOf()
            newWordIds.remove(word.id)

            wordListDetail.value = wordListDetail.value?.copy(
                wordIds = newWordIds,
                lastUpdatedAt = Date()
            )?.also { updatedList ->
                wordListRepository.updateWordList(updatedList)
                fetchWords() // Refetch words to update UI
            }
        }
    }

    fun initiateWordDetails(wordListId: Int) {
        viewModelScope.launch {
            val wordList = wordListRepository.getWordListByLocalId(wordListId)
            Log.d("WordListDetail", "Fetched word list: $wordList")
            wordListDetail.value = wordList
            if (wordList != null) {
                editTitle.value = wordList.title
                editDescription.value = wordList.description ?: ""
            }
            fetchWords()
        }
    }

    fun fetchWords() {
        viewModelScope.launch {
            val wordIds = wordListDetail.value?.wordIds
            val words = wordIds!!.mapNotNull { id ->
                wordRepository.fetchWordById(id)
            }
            _wordListWords.value = words
        }
    }
}