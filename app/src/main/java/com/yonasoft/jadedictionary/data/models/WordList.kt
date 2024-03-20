package com.yonasoft.jadedictionary.data.models

import java.util.Date

data class WordList(
    val firebaseId: String? = null,
    val title: String = "",
    val description: String? = "",
    val createdAt: Date = Date(),
    val lastUpdatedAt: Date = Date(),
    val userUid: String = "",
    val wordIds: List<Long> = emptyList()
)

fun WordList.toMap(): Map<String, Any> {
    return mapOf(
        "title" to title,
        "description" to (description ?: ""),
        "createdAt" to createdAt,
        "lastUpdatedAt" to lastUpdatedAt,
        "userUid" to userUid,
        "wordIds" to wordIds
    )
}