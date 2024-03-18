package com.yonasoft.jadedictionary.presentation.components.drawer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yonasoft.jadedictionary.data.models.NavigationItem


@Composable
fun JadeNavigationDrawerItem(
    index: Int,
    selectedItemIndex: Int,
    item: NavigationItem,
    onClickItem: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = item.title) },
        selected = index == selectedItemIndex,
        onClick =
        onClickItem,
        icon = {
            Icon(
                imageVector =
                if (index == selectedItemIndex) {
                    item.selectedIcon
                } else {
                    item.unselectedIcon
                },
                contentDescription =
                item.title,
            )
        },
        badge = {
            item.badgeCount?.let {
                item.badgeCount.toString()
            }
        },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )
}
