package com.yonasoft.jadedictionary.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "word_lists")
data class WordList(
    @PrimaryKey(autoGenerate = true) val localId: Int? = null,
    val firebaseId: String? = null,
    val title: String,
    val description: String? = "",
    val createdAt: Date,
    val lastUpdatedAt: Date,
    val userUid: String,
    val wordIds: List<Int>,
)
