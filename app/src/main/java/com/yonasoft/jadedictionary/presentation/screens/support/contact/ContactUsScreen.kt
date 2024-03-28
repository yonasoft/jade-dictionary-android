package com.yonasoft.jadedictionary.presentation.screens.support.contact

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController

@Composable
fun ContactUsScreen(navController: NavController) {
    val context = LocalContext.current
    val subject = rememberSaveable {
        mutableStateOf("")
    }
    val message = rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Contact Us",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        TopicField(topic = subject)
        MessageField(message = message)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (canSend(subject.value, message.value)) {
                    sendEmail(subject.value, message.value, context)
                }
            },
            enabled = canSend(subject.value, message.value)
        ) {
            Text(text = "Send")
        }
    }
}


private fun sendEmail(
    subject: String,
    message: String,
    context: Context
) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.data = Uri.parse("mailto:")
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("yonasoft7@gmail.com"))
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, message)
    try {
        startActivity(context, Intent.createChooser(intent, "Send Email"), null)
    } catch (e: Exception) {
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
    }
}

private fun canSend(
    topic: String,
    message: String
) = topic.isNotEmpty() && message.isNotEmpty()

@Composable
private fun MessageField(message: MutableState<String>) {
    TextField(
        value = message.value,
        onValueChange = {
            message.value = it
        },
        placeholder = { Text(text = "Message") },
        minLines = 3,
        isError = message.value.isEmpty(),
    )
    if (message.value.isEmpty()) Text(
        text = "Message can't be empty!",
        color = MaterialTheme.colorScheme.onError
    )
}

@Composable
private fun TopicField(topic: MutableState<String>) {
    TextField(
        value = topic.value,
        onValueChange = {
            topic.value = it
        },
        placeholder = { Text(text = "Topic") },
        isError = topic.value.isEmpty()
    )
    if (topic.value.isEmpty()) Text(
        text = "Topic can't be empty!",
        color = MaterialTheme.colorScheme.onError
    )
}