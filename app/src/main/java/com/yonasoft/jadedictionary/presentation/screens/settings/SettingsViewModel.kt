package com.yonasoft.jadedictionary.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonasoft.jadedictionary.data.datastore.StoreSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val storeSettings: StoreSettings
) : ViewModel() {

    // Using private MutableStateFlow for internal state management and exposing them as StateFlow
    private val _useSystemTheme = MutableStateFlow(true) // Initial value can be adjusted or fetched from StoreSettings
    val useSystemTheme: StateFlow<Boolean> = _useSystemTheme.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false) // Same as above
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        // Initialize the values from StoreSettings
        fetchCurrentSettings()
    }

    private fun fetchCurrentSettings() {
        // Collect StoreSettings values and update state flows
        viewModelScope.launch {
            storeSettings.isSystemTheme.collect { useSystem ->
                _useSystemTheme.value = useSystem
            }
        }

        viewModelScope.launch {
            storeSettings.isDarkMode.collect { isDark ->
                _isDarkMode.value = isDark
            }
        }
    }

    fun changeUseSystemTheme(useSystem: Boolean) {
        viewModelScope.launch {
            storeSettings.setSystemTheme(useSystem)
            _useSystemTheme.value = useSystem
        }
    }

    fun changeDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            storeSettings.setDarkMode(isDark)
            _isDarkMode.value = isDark
        }
    }
}