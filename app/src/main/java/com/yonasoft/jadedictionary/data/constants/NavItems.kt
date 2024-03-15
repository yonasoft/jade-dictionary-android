package com.yonasoft.jadedictionary.data.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.data.models.NavigationItem

@Composable
fun NavItems(): List<NavigationItem> {
    return listOf(
        NavigationItem(
            title = "Search",
            destination="search",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search,
        ),
        NavigationItem(
            title = "Lists",
            destination = "lists",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List,
        ),
        NavigationItem(
            title = "Practice",
            destination = "practice",
            selectedIcon = ImageVector.vectorResource(R.drawable.baseline_gamepad_24),
            unselectedIcon = ImageVector.vectorResource(R.drawable.outline_gamepad_24),
        ),
        NavigationItem(
            title = "Account",
            destination = "account",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle,
        ),
        NavigationItem(
            title = "Settings",
            destination = "settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
        ),
        NavigationItem(
            title = "Support",
            destination = "support",
            selectedIcon =  ImageVector.vectorResource(R.drawable.baseline_contact_support_24),
            unselectedIcon = ImageVector.vectorResource(R.drawable.outline_contact_support_24),
        ),
    )
}