@file:Suppress("unused")

package com.example.parental_control_app.data

import com.google.firebase.Timestamp

data class ReceivedNotification(
    val packageName: String,
    val label: String,
    val title: String,
    val content: String,
    val timestamp: Timestamp,
) {
    constructor() : this("", "", "", "", Timestamp.now())
}
