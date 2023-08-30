package com.example.parental_control_app.screens.sms

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.parental_control_app.components.MessageCard
import com.example.parental_control_app.viewmodels.sms.SmsMessageViewModel

@Composable
fun SmsMessageScreen(viewModel: SmsMessageViewModel) {
    val messages = viewModel.messagesState
    val sender = viewModel.getSender()

    Scaffold (
        topBar = { TopBar(sender = sender, onBackClick = viewModel.onBackClick) }
    ){innerPadding ->
        Surface (
            modifier = Modifier.padding(innerPadding)
        ){
            if (messages.isEmpty()) {
                Text("No Messages to show")
            } else {
                LazyColumn {
                    messages.forEach { message ->
                        item {
                            MessageCard(message)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(sender: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(sender) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Rounded.ArrowBack, "back")
            }
        }
    )
}