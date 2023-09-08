package com.example.parental_control_app.data

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

data class UserApps (
    val name: String,
    val icon: String,
    val restricted: Boolean = false,
    val screenTime: Long = 0,
) {
    constructor() : this("", "")
}

data class UserAppIcon (
    val name: String,
    val icon: Bitmap?,
)