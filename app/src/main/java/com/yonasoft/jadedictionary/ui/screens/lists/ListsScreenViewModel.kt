package com.yonasoft.jadedictionary.ui.screens.lists

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.yonasoft.jadedictionary.data.respositories.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListsScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
) :
    ViewModel() {
        var selectedTabIndex = mutableStateOf(0)
}