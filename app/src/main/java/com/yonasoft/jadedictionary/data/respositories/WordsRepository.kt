package com.yonasoft.jadedictionary.data.respositories

import com.yonasoft.jadedictionary.data.db.word.WordDao
import com.yonasoft.jadedictionary.data.models.Word
import kotlinx.coroutines.flow.Flow

class WordRepository(
    private val db: WordDao
) {
     fun searchWord(query: String): Flow<List<Word>> {
        return searchWordWithDefinition(query)
    }

     fun searchWordWithHanZi(query: String): Flow<List<Word>> {
        return db.searchWordByHanZi(query)
    }

     fun searchWordWithDefinition(query: String): Flow<List<Word>> {
        return db.searchWordByDefinition(query)
    }

     fun searchWordWithPinYin(query: String): Flow<List<Word>> {
        return db.searchWordByPinYin(query)
    }

    fun getFirstTen(): Flow<List<Word>> {
        return db.getFirstTenWords()
    }
}