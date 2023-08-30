package com.example.parental_control_app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.data.ReceivedNotification

@Composable
fun NotificationCard(packageName: String, onNotificationClick: (packageName: String) -> Unit) {
    Card (
        modifier = Modifier.fillMaxWidth().padding(10.dp).clickable { onNotificationClick(packageName) }
    ){
        Text(packageName)
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