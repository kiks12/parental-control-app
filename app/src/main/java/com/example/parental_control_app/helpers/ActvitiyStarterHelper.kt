package com.example.parental_control_app.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent

class ActivityStarterHelper(private val context: Context) {

    fun startNewActivity(activity: Class<*>) {
        val intent = Intent(context, activity)
        context.startActivity(intent)
    }

}