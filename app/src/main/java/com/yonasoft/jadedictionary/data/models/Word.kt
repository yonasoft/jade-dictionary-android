package com.yonasoft.jadedictionary.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int? = null,
    @ColumnInfo(name = "simplified") val simplified: String? = null,
    @ColumnInfo(name = "traditional") val traditional: String? = null,
    @ColumnInfo(name = "pinyin") val pinyin: String? = null,
    @ColumnInfo(name = "definition") val definition: String? = null,
    @ColumnInfo(name = "new_traditional") val newTraditional: String,
)

