package com.example.parental_control_app.managers

import android.content.SharedPreferences
import com.example.parental_control_app.repositories.users.UserProfile
import com.google.gson.Gson

class SharedPreferencesManager{

    companion object {
        const val PREFS_KEY = "Prefs"
        const val PROFILE_KEY = "Profile"
        const val BLOCKED_APPS_KEY = "BlockedApps"

        fun createJsonString(obj: Any) : String {
            val gson = Gson()
            return gson.toJson(obj)
        }

        fun getProfile(sharedPreferences: SharedPreferences) : UserProfile? {
            val gson = Gson()
            val json = sharedPreferences.getString(PROFILE_KEY, "") ?: return null
            return gson.fromJson(json, UserProfile::class.java)
        }

        fun storeBlockedApps(sharedPreferences: SharedPreferences, list: List<String>) {
            val editor = sharedPreferences.edit()
            editor.putString(BLOCKED_APPS_KEY, list.toString().substring(1, list.toString().length-1))
            editor.apply()
        }

        fun getBlockedApps(sharedPreferences: SharedPreferences) : List<String> {
            val stringList = sharedPreferences.getString(BLOCKED_APPS_KEY, "") ?: return emptyList()
            return stringList.split(", ")
        }

    }
}