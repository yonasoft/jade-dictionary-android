package com.yonasoft.jadedictionary.data.respositories

import com.yonasoft.jadedictionary.data.db.wordlist.WordListDatabase
import com.yonasoft.jadedictionary.data.models.WordList
import kotlinx.coroutines.flow.Flow

class WordListRepository(private val database: WordListDatabase) {

    private val wordListDao = database.wordListDao()

    fun getAllWordLists(): Flow<List<WordList>> = wordListDao.getAllWordLists()

    suspend fun insertWordList(wordList: WordList) = wordListDao.insertWordList(wordList)

    suspend fun updateWordList(wordList: WordList) = wordListDao.updateWordList(wordList)

    suspend fun deleteWordList(wordList: WordList) = wordListDao.deleteWordList(wordList)

    suspend fun getWordListById(id: String): WordList? = wordListDao.getWordListById(id)
}
