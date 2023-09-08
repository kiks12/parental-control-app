package com.example.parental_control_app.service

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.parental_control_app.R
import com.example.parental_control_app.activities.LockActivity
import com.example.parental_control_app.activities.children.ChildrenMainActivity

class AppLockerService : Service(){

    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable {}
    private val checkInterval = 500

    private fun getCurrentRunningAppPackageName(context: Context): String? {
        val currentTime = System.currentTimeMillis()
        val usageStatsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP_MR1")
        }

        val usageEvents = usageStatsManager.queryEvents(currentTime - 2000, System.currentTimeMillis())
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                return event.packageName
            }
        }

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val blockedApps = intent?.getStringArrayExtra(ChildrenMainActivity.BLOCKED_APPS_KEY)
        val notification = NotificationCompat.Builder(this, "app_locker_channel")
            .setContentTitle("App Locker")
            .setContentText("Locking Blocked Apps")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        monitorRunningApp(blockedApps?.toList())

        return START_STICKY
    }

    private fun monitorRunningApp(blockedAppNames : List<String>?) {
        runnable = Runnable {
            val currentAppPackageName = getCurrentRunningAppPackageName(applicationContext)
            Log.w("APP LOCK SERVICE B", blockedAppNames.toString())
            if (currentAppPackageName != null) {
                if (blockedAppNames != null && blockedAppNames.contains(currentAppPackageName)) {
                    Log.w("APP LOCK SERVICE", "Current Running App Package Name: $currentAppPackageName")
                    val intent = Intent(applicationContext, LockActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    applicationContext.startActivity(intent)
                }
            } else {
                Log.w("APP LOCK SERVICE","No running app found.")
            }

            monitorRunningApp(blockedAppNames)
        }

        handler.postDelayed(runnable, checkInterval.toLong())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            handler.removeCallbacks(runnable)
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }
}