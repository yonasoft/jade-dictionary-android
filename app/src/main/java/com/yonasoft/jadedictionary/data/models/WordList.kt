package com.yonasoft.jadedictionary.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "word_lists")
data class WordList(
    @PrimaryKey(autoGenerate = true) val localId: Int? = null,
    @ColumnInfo(name="firebaseId")
    val firebaseId: String? = null,
    @ColumnInfo(name="title")
    val title: String,
    @ColumnInfo(name="description")
    val description: String? = "",
    @ColumnInfo(name="createdAt")
    val createdAt: Date,
    @ColumnInfo(name="updatedAt")
    val lastUpdatedAt: Date,
    @ColumnInfo(name="userUid")
    val userUid: String,
    @ColumnInfo(name="wordIds")
    val wordIds: List<Int>,
)
