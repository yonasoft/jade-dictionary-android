package com.yonasoft.jadedictionary.data.db

import androidx.room.*
import com.yonasoft.jadedictionary.data.models.WordList
import kotlinx.coroutines.flow.Flow

@Dao
interface WordListDAO {
    @Query("SELECT * FROM word_lists")
    fun getAllWordLists(): Flow<List<WordList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordList(wordList: WordList)

    @Update
    suspend fun updateWordList(wordList: WordList)

    @Delete
    suspend fun deleteWordList(wordList: WordList)

    @Query("SELECT * FROM word_lists WHERE localId = :localId")
    suspend fun getWordListById(localId: Int): WordList?

    @Query(
        """
    SELECT * FROM word_lists
    WHERE title LIKE '%' || :searchQuery || '%' 
    OR description LIKE '%' || :searchQuery || '%'
    ORDER BY 
        CASE 
            WHEN title = :searchQuery THEN 1
            WHEN title LIKE :searchQuery || '%' THEN 2
            WHEN title LIKE '%' || :searchQuery || '%' THEN 3
            WHEN description = :searchQuery THEN 4
            WHEN description LIKE :searchQuery || '%' THEN 5
            ELSE 6
        END,
        LENGTH(title), LENGTH(description)
"""
    )
    fun searchWordListsByTitleOrDescription(searchQuery: String): Flow<List<WordList>>
}
