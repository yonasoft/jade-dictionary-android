package com.yonasoft.jadedictionary.presentation.screens.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AllInclusive
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.yonasoft.jadedictionary.data.models.TabItem

@Composable
fun ListsScreen(viewModel: ListsScreenViewModel = hiltViewModel()) {

    val selectedTabIndex = viewModel.selectedTabIndex

    val tabItems = listOf(
        TabItem(
            title = "ALl",
            unselectedIcon =  Icons.Outlined.AllInclusive,
            selectedIcon = Icons.Filled.AllInclusive,
        ),
        TabItem(
            title = "Local",
            unselectedIcon =  Icons.Outlined.Storage,
            selectedIcon = Icons.Filled.Storage,
        )
        ,
        TabItem(
            title = "Account",
            unselectedIcon =  Icons.Outlined.AccountCircle,
            selectedIcon = Icons.Filled.AccountCircle,
        )
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(selectedTabIndex = selectedTabIndex.value){

        }
    }
}