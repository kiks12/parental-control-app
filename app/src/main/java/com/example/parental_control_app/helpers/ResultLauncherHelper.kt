package com.example.parental_control_app.helpers

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

class ResultLauncherHelper(
    private val context: Context,
    private val resultLauncher: ActivityResultLauncher<Intent>
){
    fun launch(anyClass: Class<*>) {
        resultLauncher.launch(Intent(context, anyClass))
    }
}