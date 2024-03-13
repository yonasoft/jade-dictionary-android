package com.yonasoft.jadedictionary.data.db.word

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yonasoft.jadedictionary.data.models.Word

@Database(entities = [Word::class], version = 1)
abstract class WordDatabase : RoomDatabase() {
    abstract fun dao(): WordDao
}
