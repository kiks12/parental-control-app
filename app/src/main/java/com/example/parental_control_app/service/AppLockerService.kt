package com.example.parental_control_app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.parental_control_app.R
import com.example.parental_control_app.activities.children.ChildrenMainActivity
import com.example.parental_control_app.workers.AppLockWorker

class AppLockerService : Service(){

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = NotificationCompat.Builder(this, "app_locker_channel")
            .setContentTitle("App Locker")
            .setContentText("Locking Blocked Apps")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        val list = intent?.getStringArrayExtra(ChildrenMainActivity.BLOCKED_APPS_KEY)
        val data = Data.Builder()
            .putStringArray(ChildrenMainActivity.BLOCKED_APPS_KEY, list!!)
            .build()

        val workerRequest : WorkRequest = OneTimeWorkRequest.Builder(AppLockWorker::class.java)
            .setInputData(data)
            .build()
        WorkManager.getInstance(this).enqueue(workerRequest)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        WorkManager.getInstance(this).cancelAllWork()
    }
}