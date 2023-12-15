package com.example.parental_control_app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UninstallationService : Service() {

    private val usersRepository = UsersRepository()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.w("DISABLED", "CREATING")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        Log.w("DISABLED", "CREATED")
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val uid = SharedPreferencesManager.getUID(sharedPreferences)

        GlobalScope.launch(Dispatchers.Main) {
            // Perform Firebase queries or other tasks
            if (uid == null) return@launch
            performAsyncTasks(uid)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private suspend fun performAsyncTasks(uid: String) {
        // Example: Perform asynchronous tasks (e.g., Firebase queries) here
        try {
            usersRepository.saveUninstalledStatus(uid, true)
        } catch (e: Exception) {
            Log.e("ForegroundService", "Error during Firebase query: ${e.message}")
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Running background tasks...")
            .build()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val uninstallationChannel = NotificationChannel(CHANNEL_ID, "Uninstallation Channel", importance)
        uninstallationChannel.description = ""

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(uninstallationChannel)
    }

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "ForegroundServiceChannel"
    }
}