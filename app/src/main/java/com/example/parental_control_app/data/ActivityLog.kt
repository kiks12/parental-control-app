@file:Suppress("unused")

package com.example.parental_control_app.data

import com.google.firebase.Timestamp

data class ActivityLog (
    val packageName: String,
    val label: String,
    val datetime: Timestamp,
) {
    constructor() : this("", "", Timestamp.now())
}
