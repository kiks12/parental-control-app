package com.example.parental_control_app.helpers

import android.content.SharedPreferences
import com.example.parental_control_app.repositories.users.UserProfile
import com.google.gson.Gson

class SharedPreferencesHelper{
    companion object {
        val PREFS_KEY = "Prefs"
        val PROFILE_KEY = "Profile"

        fun createJsonString(obj: Any) : String {
            val gson = Gson()
            return gson.toJson(obj)
        }

        fun getProfile(sharedPreferences: SharedPreferences) : UserProfile? {
            val gson = Gson()
            val json = sharedPreferences.getString(PROFILE_KEY, "")
            if (json == null) return null
            return gson.fromJson(json, UserProfile::class.java)
        }
    }
}