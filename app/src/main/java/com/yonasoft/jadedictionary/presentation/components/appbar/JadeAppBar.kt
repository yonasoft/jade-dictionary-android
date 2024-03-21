@file:OptIn(ExperimentalMaterial3Api::class)

package com.yonasoft.jadedictionary.presentation.components.appbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.yonasoft.jadedictionary.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun JadeAppBar(scope: CoroutineScope, drawerState: DrawerState, navController: NavController) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val canNavigateUp =
        currentBackStackEntry != null && navController.previousBackStackEntry != null

    CenterAlignedTopAppBar(

        modifier = Modifier
            .shadow(8.dp),
        title = {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.jade_logo),
                contentDescription = "Image",
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topEnd = 8.dp,
                            topStart = 8.dp
                        )
                    )
                    .fillMaxHeight()
                    .width(100.dp),
                contentScale = ContentScale.Inside,
            )
        },
        navigationIcon = {
            if (canNavigateUp) {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Arrow Back",
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu Icon",
                )
            }
        },
    )
}



