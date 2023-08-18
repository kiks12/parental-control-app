package com.example.parental_control_app.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent

class ActivityStarterHelper(private val context: Context) {

    fun startNewActivity(activity: Class<*>, extras: Map<String, String>? = null) {
        val intent = Intent(context, activity)
        if (extras != null) {
            extras.values.forEachIndexed { index, string ->
                intent.putExtra(extras.keys.toList()[index], string)
            }
        }
        context.startActivity(intent)
    }

}