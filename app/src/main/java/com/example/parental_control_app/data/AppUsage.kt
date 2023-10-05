package com.example.parental_control_app.data

import android.content.pm.ApplicationInfo

data class AppUsage (
    val packageName: String,
    val screenTime: Long,
    val app : ApplicationInfo? = null,
)
