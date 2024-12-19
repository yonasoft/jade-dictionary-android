package com.yonasoft.jadedictionary.presentation.screens.shared

import com.google.firebase.auth.FirebaseAuth
import com.yonasoft.jadedictionary.data.models.WordList

data class SharedAppState(
    val isNetworkAvailable:Boolean = false,
    val auth:FirebaseAuth? = null,
    val userWordLists:List<WordList> = emptyList(),
    val searchHistory:List<String> = emptyList()
)
