package com.example.parental_control_app.broadcast.receivers

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.service.UninstallationService
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChildDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        val sharedPreferences = context.getSharedPreferences(SharedPreferencesManager.PREFS_KEY, Context.MODE_PRIVATE)
        val profile = SharedPreferencesManager.getProfile(sharedPreferences)

        if (isConnected(context)) {
            Log.w("DISABLED", "CONNECTED")
            val serviceIntent = Intent(context, UninstallationService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
            Log.w("DISABLED", "CONNECTED")
        } else {
            Log.w("DISABLED", "DISCONNECTED")
            val editor = sharedPreferences.edit()
            editor.putBoolean(SharedPreferencesManager.UNINSTALLED_KEY, true)
            editor.apply()
        }
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities =
            connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }
}