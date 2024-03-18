package com.yonasoft.jadedictionary.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "word_lists")
data class WordList(
    @PrimaryKey val id: String, // Same as Firebase ID
    val title: String,
    val description: String? = "",
    val createdAt: Date,
    val lastUpdatedAt: Date,
    val userUid: String,
    val wordIds: List<Int> // Assuming word IDs are integers; adjust as necessary
)