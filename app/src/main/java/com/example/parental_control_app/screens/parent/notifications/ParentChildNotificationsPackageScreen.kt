package com.example.parental_control_app.screens.parent.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.data.ReceivedNotification

@Composable
fun ParentChildNotificationPackageScreen(notifications: List<ReceivedNotification>) {
    if (notifications.isEmpty()) {
        Text("No Notifications to show")
    } else {
        LazyColumn {
            notifications.forEach { notification ->
                item { NotificationPackageCard(notification) }
            }
        }
    }
}

@Composable
fun NotificationPackageCard(notification: ReceivedNotification) {
    Card (
        modifier = Modifier.padding(10.dp).fillMaxWidth()
    ){
        Column {
            Text(notification.title)
            Text(notification.content)
        }
    }
}