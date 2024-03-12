package com.yonasoft.jadedictionary.data.models

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val destination:String,
    val badgeCount: Int? = null
)
