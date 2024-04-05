package com.yonasoft.jadedictionary.presentation.screens.support

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yonasoft.jadedictionary.data.constants_and_sealed.Screen

val links =
    listOf(Screen.Contact, Screen.Donate)

@Composable
fun SupportScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Support",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                links.size
            ) {
                val link = links[it]
                SupportLinkCard(navController = navController, screen = link)
            }
        }
    }
}

@Composable
fun SupportLinkCard(navController: NavController, screen: Screen) {
    return Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(184.dp)
            .padding(8.dp)
            .clickable {
                navController.navigate(screen.route)
            },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = screen.name!!,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}