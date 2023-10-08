package com.example.parental_control_app.data

import android.graphics.Bitmap

@Suppress("unused")
data class UserApps (
    val packageName: String,
    val label: String = "",
    val restricted: Boolean = false,
    val screenTime: Long = 0,
    val limit: Long = 0,
) {
    constructor() : this("")
}

data class UserAppIcon (
    val name: String,
    val icon: Bitmap?,
)