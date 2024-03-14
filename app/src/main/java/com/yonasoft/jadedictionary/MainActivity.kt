package com.yonasoft.jadedictionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.yonasoft.jadedictionary.data.constants.NavItems
import com.yonasoft.jadedictionary.data.models.NavigationItem
import com.yonasoft.jadedictionary.ui.components.appbar.JadeAppBar
import com.yonasoft.jadedictionary.ui.components.drawer.JadeModalDrawerSheet
import com.yonasoft.jadedictionary.ui.theme.JadeDictionaryTheme
import com.yonasoft.jadedictionary.util.setupNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JadeDictionaryTheme {
                JadeDictionaryApp()
            }
        }
    }

}

@Composable
fun JadeDictionaryApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val navDrawerItems: List<NavigationItem> = NavItems()
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        ModalNavigationDrawer(
            drawerContent = {
                JadeModalDrawerSheet(
                    navItems = navDrawerItems,
                    selectedItemIndex = selectedItemIndex,
                    onSelectItem = {
                        selectedItemIndex = it
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    navController = navController
                )
            },
            drawerState = drawerState
        ) {
            Scaffold(
                topBar = {
                    JadeAppBar(
                        scope = scope,
                        drawerState = drawerState,
                        navController = navController
                    )
                },
                modifier = Modifier
            ) {
                Surface(modifier = Modifier.padding(it)) {
                    setupNavigation(navController = navController)
                }
            }
        }
    }
}

