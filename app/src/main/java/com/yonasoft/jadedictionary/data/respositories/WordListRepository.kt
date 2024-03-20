package com.yonasoft.jadedictionary.data.respositories

import com.yonasoft.jadedictionary.data.db.WordListDAO
import com.yonasoft.jadedictionary.data.models.WordList
import kotlinx.coroutines.flow.Flow

class WordListRepository(private val dao: WordListDAO) {

    fun getAllWordLists(): Flow<List<WordList>> = dao.getAllWordLists()

    suspend fun insertWordList(wordList: WordList) = dao.insertWordList(wordList)

    suspend fun updateWordList(wordList: WordList) = dao.updateWordList(wordList)

    suspend fun deleteWordList(wordList: WordList) = dao.deleteWordList(wordList)

    suspend fun getWordListByLocalId(id: Int): WordList? = dao.getWordListById(id)

    suspend fun  searchWordList(searchQuery: String): Flow<List<WordList>> = dao.searchWordListsByTitleOrDescription(searchQuery)
}
