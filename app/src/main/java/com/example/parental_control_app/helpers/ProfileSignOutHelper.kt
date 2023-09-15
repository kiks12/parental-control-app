package com.example.parental_control_app.helpers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.parental_control_app.activities.StartupActivity
import com.example.parental_control_app.managers.SharedPreferencesManager

class ProfileSignOutHelper(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
){
    fun signOut() {
        sharedPreferences.edit().remove(SharedPreferencesManager.PROFILE_KEY).apply()
        val intent = Intent(context, StartupActivity::class.java)
        context.startActivity(intent)
    }
}