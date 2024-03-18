package com.yonasoft.jadedictionary.data.db

import androidx.room.*
import com.yonasoft.jadedictionary.data.models.WordList
import kotlinx.coroutines.flow.Flow

@Dao
interface WordListDao {
    @Query("SELECT * FROM word_lists")
    fun getAllWordLists(): Flow<List<WordList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordList(wordList: WordList)

    @Update
    suspend fun updateWordList(wordList: WordList)

    @Delete
    suspend fun deleteWordList(wordList: WordList)

    @Query("SELECT * FROM word_lists WHERE id = :id")
    suspend fun getWordListById(id: String): WordList?

    // Additional queries for search, etc.
}
