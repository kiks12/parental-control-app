package com.example.parental_control_app.data

import android.app.Notification
import com.google.firebase.Timestamp

data class ReceivedNotification(
    val packageName: String,
    val notification: Notification,
    val timestamp: Timestamp,
)
