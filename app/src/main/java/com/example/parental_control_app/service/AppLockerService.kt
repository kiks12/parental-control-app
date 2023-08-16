package com.example.parental_control_app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.parental_control_app.R
import com.example.parental_control_app.workers.AppLockWorker
import java.util.concurrent.TimeUnit

class AppLockerService : Service(){
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.w("START SERVICE", "Starting Service...")
        val notification = NotificationCompat.Builder(this, "app_locker_channel")
            .setContentTitle("App Locker")
            .setContentText("adkfjashdfjksdf")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        val workerRequest : WorkRequest = OneTimeWorkRequestBuilder<AppLockWorker>().build()
        WorkManager.getInstance(this).enqueue(workerRequest)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}