package com.yonasoft.jadedictionary.data.models

import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    val title:String,
    val unselectedIcon: ImageVector? = null,
    val selectedIcon: ImageVector? = null,
    )