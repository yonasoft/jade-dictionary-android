package com.yonasoft.jadedictionary.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.data.datastore.StoreSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val storeSettings: StoreSettings
) : ViewModel() {

    private val _useSystemTheme = MutableStateFlow(true)
    val useSystemTheme: StateFlow<Boolean> = _useSystemTheme.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        fetchCurrentSettings()
    }

    private fun fetchCurrentSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            storeSettings.isSystemTheme.collect { useSystem ->
                withContext(Dispatchers.Main){
                    _useSystemTheme.value = useSystem
                }
            }
            storeSettings.isDarkMode.collect { isDark ->
                withContext(Dispatchers.Main) {
                    _isDarkMode.value = isDark
                }
            }
        }
    }

    fun changeUseSystemTheme(useSystem: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            storeSettings.setSystemTheme(useSystem)
            withContext(Dispatchers.Main) {
                _useSystemTheme.value = useSystem
            }
        }
    }

    fun changeDarkMode(isDark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            storeSettings.setDarkMode(isDark)
            withContext(Dispatchers.Main) {
                _isDarkMode.value = isDark
            }
        }
    }
}