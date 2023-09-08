package com.example.parental_control_app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.data.Sms
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SmsCard(sender: String, onSmsClick: (documentId: String) -> Unit) {
    ListItem(
        headlineContent = { Text(sender) },
        leadingContent = { Icon(Icons.Rounded.Person, sender) },
        trailingContent = { Icon(Icons.Rounded.KeyboardArrowRight, sender) },
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable { onSmsClick(sender) }
    )
}

@Composable
fun MessageCard(message: Sms) {
    val dateTime = SimpleDateFormat("MM/dd/yyyy - hh:mm:ss", Locale.ROOT)

    ListItem(
        headlineContent = { Text(message.messageBody) },
        supportingContent = { Text(dateTime.format(message.timestamp.toDate())) },
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}