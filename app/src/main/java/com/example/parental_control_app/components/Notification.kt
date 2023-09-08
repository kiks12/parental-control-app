package com.example.parental_control_app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.data.ReceivedNotification
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NotificationCard(packageName: String, onNotificationClick: (packageName: String) -> Unit) {
    ListItem(
        headlineContent = { Text(packageName) },
        trailingContent = { Icon(Icons.Rounded.KeyboardArrowRight, packageName)},
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable { onNotificationClick(packageName) }
    )
}

@Composable
fun NotificationPackageCard(notification: ReceivedNotification) {
    val dateTime = SimpleDateFormat("MM/dd/yyyy\nhh:mm:ss", Locale.ROOT)

    ListItem(
        headlineContent = { Text(notification.title) },
        supportingContent = { Text(notification.content) },
        trailingContent = { Text(dateTime.format(notification.timestamp.toDate())) }
    )
}