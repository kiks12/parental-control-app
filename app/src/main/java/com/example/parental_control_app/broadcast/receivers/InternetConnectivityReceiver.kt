package com.example.parental_control_app.broadcast.receivers

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class InternetConnectivityReceiver(private val context: Context, private val profileId: String) {

    private val usersRepository : UsersRepository = UsersRepository()
    private val job = SupervisorJob()
    private val coroutine = CoroutineScope(Dispatchers.IO + job)

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            val sharedPreferences = context.getSharedPreferences(SharedPreferencesManager.PREFS_KEY, Context.MODE_PRIVATE)
            val uninstalled = SharedPreferencesManager.getUninstalledStatus(sharedPreferences)

            Log.d(TAG, "Internet connected")

            if (uninstalled) {
                coroutine.launch {
                    val uid = usersRepository.getProfileUID(profileId)
                    usersRepository.saveUninstalledStatus(uid, true)
                    val editor = sharedPreferences.edit()
                    editor.remove(SharedPreferencesManager.UNINSTALLED_KEY)
                    editor.apply()
                }
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d(TAG, "Internet disconnected")
        }
    }

    fun register() {
        val networkRequest = android.net.NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

//    fun unregister() {
//        connectivityManager.unregisterNetworkCallback(networkCallback)
//    }

    companion object {
        private const val TAG = "InternetConnectivityReceiver"
    }

}