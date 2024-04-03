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
        // Collect StoreSettings values and update state flows
        viewModelScope.launch(Dispatchers.IO) {
            storeSettings.isSystemTheme.collect { useSystem ->
                _useSystemTheme.value = useSystem
            }
        }

        viewModelScope.launch(Dispatchers.IO)  {
            storeSettings.isDarkMode.collect { isDark ->
                _isDarkMode.value = isDark
            }
        }
    }

    fun changeUseSystemTheme(useSystem: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            storeSettings.setSystemTheme(useSystem)
            _useSystemTheme.value = useSystem
        }
    }

    fun changeDarkMode(isDark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            storeSettings.setDarkMode(isDark)
            _isDarkMode.value = isDark
        }
    }
}