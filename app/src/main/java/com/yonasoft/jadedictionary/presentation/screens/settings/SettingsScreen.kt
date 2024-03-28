package com.yonasoft.jadedictionary.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val useSystemTheme = viewModel.useSystemTheme.collectAsState()
    val isDarkMode = viewModel.isDarkMode.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SystemThemeRow(useSystemTheme = useSystemTheme, changeUseSystemTheme = {
            viewModel.changeUseSystemTheme(it)
        })
        ThemeModeRow(
            isDarkMode = isDarkMode.value,
            onToggleDarkMode = { viewModel.changeDarkMode(it) },
            enabled = !useSystemTheme.value // Disable this row if system theme is used
        )
        Divider(modifier = Modifier.height(1.dp))
    }
}

@Composable
fun SystemThemeRow(
    useSystemTheme: State<Boolean>,
    changeUseSystemTheme: (useSystem: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Use System Theme")
        Checkbox(
            checked = useSystemTheme.value,
            onCheckedChange = { changeUseSystemTheme(it) }
        )
    }
}

@Composable
fun ThemeModeRow(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Light/Dark Mode")
        IconToggleButton(
            checked = isDarkMode,
            onCheckedChange = onToggleDarkMode,
            enabled = enabled
        ) {
            val icon: ImageVector =
                if (isDarkMode) Icons.Filled.Brightness3 else Icons.Filled.Brightness5
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}

@Composable
fun IconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    IconButton(onClick = { onCheckedChange(!checked) }, enabled = enabled) {
        content()
    }
}