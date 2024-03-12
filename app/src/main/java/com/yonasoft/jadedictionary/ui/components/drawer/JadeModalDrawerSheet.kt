package com.yonasoft.jadedictionary.ui.components.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.data.models.NavigationItem

@Composable
fun JadeModalDrawerSheet(
    navItems: List<NavigationItem>,
    selectedItemIndex: Int,
    onSelectItem: (index: Int) -> Unit,
    navController: NavController,
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            navItems.forEachIndexed { index, item ->
                JadeNavigationDrawerItem(
                    index = index,
                    selectedItemIndex = selectedItemIndex,
                    item = item,
                    onClickItem = {
                        onSelectItem(index)
                        navController.navigate(item.destination) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                )
            }
        }
    }
}