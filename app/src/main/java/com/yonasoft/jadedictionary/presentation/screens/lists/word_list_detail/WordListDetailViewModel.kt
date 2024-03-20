package com.yonasoft.jadedictionary.presentation.screens.lists.word_list_detail

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.data.respositories.WordListRepository
import com.yonasoft.jadedictionary.data.respositories.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WordListDetailViewModel @Inject constructor(
    private val wordListRepository: WordListRepository,
    private val wordRepository: WordRepository,
) : ViewModel() {

    val editTitle = mutableStateOf("")
    val editDescription = mutableStateOf("")
    private val wordListDetail = mutableStateOf<WordList?>(null)
    private val _wordListWords = MutableStateFlow<List<Word>>(emptyList())
    val wordListWords = _wordListWords.asStateFlow()

    fun removeWord(word: Word) {
        viewModelScope.launch {
            val newWordIds = wordListDetail.value?.wordIds?.toMutableList() ?: mutableListOf()
            newWordIds.remove(word.id)

            wordListDetail.value = wordListDetail.value?.copy(
                wordIds = newWordIds,
                lastUpdatedAt = Date()
            )?.also { updatedList ->
                wordListRepository.addOrUpdateWordList(updatedList)
                fetchWords()
            }
        }
    }

    fun initiateWordDetails(wordListId: String) {
        viewModelScope.launch {
            Log.i("ids", "$wordListId")
            val wordList = wordListRepository.getWordListById(wordListId)
            if(wordList!=null) {
                wordListDetail.value = wordList
                editTitle.value = wordList!!.title
                editDescription.value = wordList.description ?: ""
                fetchWords()
            }
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

    fun saveWordList() {
        viewModelScope.launch {
            val newWordList = wordListDetail.value!!.copy(
                title = editTitle.value,
                description = editDescription.value,
                lastUpdatedAt = Date(),
            )
            wordListRepository.addOrUpdateWordList(newWordList)
        }
    }
}