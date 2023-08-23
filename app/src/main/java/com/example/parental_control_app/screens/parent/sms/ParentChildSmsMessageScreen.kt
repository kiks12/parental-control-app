package com.example.parental_control_app.screens.parent.sms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.data.Sms

@Composable
fun ParentChildSmsMessageScreen(messages: List<Sms>) {
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

@Composable
fun MessageCard(message: Sms) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ){
        Column {
            Text(message.messageBody)
            Text(
                message.timestamp.toDate().toString(),
                fontSize = 10.sp
            )
        }
    }
}