package com.example.parental_control_app.managers

import android.content.SharedPreferences
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.repositories.users.UserProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.TimeUnit

class SharedPreferencesManager{

    companion object {
        const val PREFS_KEY = "Prefs"
        const val PROFILE_KEY = "Profile"
        const val UID_KEY = "UID"
        const val PIN_KEY = "PIN"
        const val DEVICE_NAME_SAVED_KEY = "DEVICE_NAME"
        const val TIMER_KEY = "TIMER_KEY"
        const val UNINSTALLED_KEY = "UNINSTALLED_KEY"
        private const val BLOCKED_APPS_LIMIT_KEY = "BlockedAppsLimit"



        fun createJsonString(obj: Any) : String {
            val gson = Gson()
            return gson.toJson(obj)
        }

        fun getProfile(sharedPreferences: SharedPreferences) : UserProfile? {
            val gson = Gson()
            val json = sharedPreferences.getString(PROFILE_KEY, "") ?: return null
            return gson.fromJson(json, UserProfile::class.java)
        }

        fun storeBlockedApps(sharedPreferences: SharedPreferences, list: List<UserApps>) {
            val gson = Gson()
            val editor = sharedPreferences.edit()
            editor.putString(BLOCKED_APPS_LIMIT_KEY, gson.toJson(list))
            editor.apply()
        }

        fun getBlockedApps(sharedPreferences: SharedPreferences): List<UserApps> {
            val gson = Gson()
            val json = sharedPreferences.getString(BLOCKED_APPS_LIMIT_KEY, "") ?: return emptyList()
            val typeToken = object : TypeToken<List<UserApps>>() {}.type

            return gson.fromJson(json, typeToken)
        }

        fun getUID(sharedPreferences: SharedPreferences) : String? {
            return sharedPreferences.getString(UID_KEY, "")
        }

        fun getPIN(sharedPreferences: SharedPreferences) : String? {
            return sharedPreferences.getString(PIN_KEY, "")
        }

        fun isDeviceSaved(sharedPreferences: SharedPreferences) : Boolean {
            return sharedPreferences.getBoolean(DEVICE_NAME_SAVED_KEY, false)
        }

        fun genTimer(sharedPreferences: SharedPreferences) : Long {
            return sharedPreferences.getLong(TIMER_KEY, 0L)
        }

        fun getUninstalledStatus(sharedPreferences: SharedPreferences) : Boolean {
            return sharedPreferences.getBoolean(UNINSTALLED_KEY, false)
        }
    }
}