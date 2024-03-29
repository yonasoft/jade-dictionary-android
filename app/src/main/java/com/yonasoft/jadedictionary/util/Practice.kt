package com.yonasoft.jadedictionary.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Practice {
    @Composable
    fun mapIconFromAnswer(category: String): (@Composable () -> Unit)? {
        return when (category) {
            "Hard", "Incorrect" -> {
                @Composable {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hard",
                        tint = MaterialTheme.colorScheme.error
                    )

                }
            }
            "Easy", "Correct" -> {
                @Composable {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Easy",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            "Medium" -> {
                @Composable {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = "Medium",
                        tint = Color.Yellow
                    )
                }
            }
            else -> {
                null
            }
        }
    }
}