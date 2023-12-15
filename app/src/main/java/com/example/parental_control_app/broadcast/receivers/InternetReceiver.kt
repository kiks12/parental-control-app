package com.example.parental_control_app.broadcast.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

class InternetReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            else -> {
                Log.w("NETWORK CHANGE", "SJKDFHSKjfd")
                if (intent?.extras != null) {
                    if (isConnected(context!!)) {
                        Log.d("NetworkChangeReceiver", "Internet Connected")
                    } else {
                        Log.d("NetworkChangeReceiver", "Internet Disconnected")
                    }
                }
            }
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