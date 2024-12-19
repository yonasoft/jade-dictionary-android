package com.yonasoft.jadedictionary.presentation.screens.shared

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yonasoft.jadedictionary.data.datastore.StoreSearchHistory
import com.yonasoft.jadedictionary.data.models.Word
import com.yonasoft.jadedictionary.data.models.WordList
import com.yonasoft.jadedictionary.data.respositories.FirebaseAuthRepository
import com.yonasoft.jadedictionary.data.respositories.WordListRepository
import com.yonasoft.jadedictionary.data.respositories.WordRepository
import com.yonasoft.jadedictionary.util.Network.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SharedAppViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: FirebaseAuthRepository,
    private val wordRepository: WordRepository,
    private val wordListRepository: WordListRepository,
    private val storeSearchHistory: StoreSearchHistory,
) :
    ViewModel() {
    private val _sharedAppState = MutableStateFlow(SharedAppState())
    val sharedAppState: StateFlow<SharedAppState> = _sharedAppState

    init {
        initializeSharedAppState()
        observeAuthState()
    }

    private fun initializeSharedAppState() {
        viewModelScope.launch(Dispatchers.IO) {
            val isNetworkAvailable = isNetworkAvailable(context)
            val auth = authRepository.getAuth()
            val userWordLists = getUserWordLists(auth.currentUser != null)
            val searchHistory = storeSearchHistory.getSearchHistorySync()

            _sharedAppState.update { state ->
                state.copy(
                    isNetworkAvailable = isNetworkAvailable,
                    auth = auth,
                    userWordLists = userWordLists,
                    searchHistory = searchHistory,
                )
            }
        }
    }

    private fun observeAuthState() {
        FirebaseAuth.getInstance().addAuthStateListener { newAuth ->
            Log.d("AuthState", "Auth state changed: ${newAuth.currentUser}")
            _sharedAppState.update { state ->
                state.copy(auth = newAuth)
            }
        }
    }

    fun updateAuthState(newAuth: FirebaseAuth = FirebaseAuth.getInstance()) {
        _sharedAppState.update { state ->
            state.copy(auth = newAuth)
        }
    }

    fun fetchWordById(
        id: Long,
        onResult: (word: Word?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val word = wordRepository.fetchWordById(id)
            withContext(Dispatchers.Main) {
                onResult(word)
            }
        }
    }

    private suspend fun getUserWordLists(isLoggedIn: Boolean): List<WordList> {
        var userLists = emptyList<WordList>()
        if (isLoggedIn) wordListRepository.getWordLists().collect {
            userLists = it
        }
        return userLists
    }

    fun fetchUserWordLists(
        isLoggedIn: Boolean,
        onResult: (List<WordList>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userLists = getUserWordLists(isLoggedIn)
            withContext(Dispatchers.Main) {
                onResult(userLists)
            }
        }
    }

    fun addToHistory(newSearch: String, onResult: (List<String>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val newHistory = listOf(*sharedAppState.value.searchHistory.toTypedArray(), newSearch)
            storeSearchHistory.storeSearchHistory(newHistory)
            withContext(Dispatchers.Main) {
                onResult(newHistory)
            }
        }
    }

    fun removeFromHistory(index: Int, onResult: (List<String>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val newHistory = mutableListOf(*sharedAppState.value.searchHistory.toTypedArray())
            newHistory.removeAt(index)
            storeSearchHistory.storeSearchHistory(newHistory as List<String>)
            withContext(Dispatchers.Main) {
                onResult(newHistory as List<String>)
            }
        }
    }
}