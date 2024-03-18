package com.yonasoft.jadedictionary.data.db.wordlist

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yonasoft.jadedictionary.data.db.WordListDao
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.util.Converters

@Database(entities = [WordList::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WordListDatabase : RoomDatabase() {
    abstract fun wordListDao(): WordListDao
}