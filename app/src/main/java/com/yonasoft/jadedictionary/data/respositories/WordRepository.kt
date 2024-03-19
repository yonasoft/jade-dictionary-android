package com.yonasoft.jadedictionary.data.respositories

import android.util.Log
import com.yonasoft.jadedictionary.data.constants.StringType
import com.yonasoft.jadedictionary.data.db.word.WordDAO
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.util.determineStringType
import com.yonasoft.jadedictionary.util.normalizePinyinInput
import kotlinx.coroutines.flow.Flow

class WordRepository(private val dao: WordDAO) {
    fun searchWord(query: String): Flow<List<Word>> {
        val lowercaseQuery = query.lowercase()
        Log.i("query_type", "function ran")
        val type = determineStringType(lowercaseQuery)
        Log.i("query_type", type.toString())
        Log.i("query_type", lowercaseQuery)
        return when (type) {
            StringType.Hanzi -> searchWordWithHanZi(lowercaseQuery.replace(" ", ""))
            StringType.Pinyin -> searchWordWithPinYin(lowercaseQuery)
            StringType.English -> searchWordWithDefinition(lowercaseQuery)
        }
    }

    private fun searchWordWithHanZi(query: String): Flow<List<Word>> {
        return dao.searchWordByHanZi(query)
    }

    private fun searchWordWithDefinition(query: String): Flow<List<Word>> {
        Log.i("query_type", "begin def search")
        return dao.searchWordByDefinition(query)
    }

    private fun searchWordWithPinYin(query: String): Flow<List<Word>> {
        val normalizedQuery = normalizePinyinInput(query)
        return dao.searchWordByPinYin(normalizedQuery)
    }

    suspend fun fetchWordById(id: Int): Word? {
        return dao.getWordById(id)
    }


}
