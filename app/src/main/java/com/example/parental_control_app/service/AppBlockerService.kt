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
//import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.parental_control_app.R
import com.example.parental_control_app.activities.stacking.BlockActivity
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.managers.SharedPreferencesManager
import java.util.Calendar

data class AppUsage (
    val packageName: String,
    val screenTime: Long,
)

class AppBlockerService : Service(){

    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable {}
    private val checkInterval = 500

    private fun getCurrentRunningAppPackageName(context: Context): AppUsage? {
        val usageStatsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP_MR1")
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val interval = UsageStatsManager.INTERVAL_DAILY
        val usageStatsList = usageStatsManager.queryUsageStats(interval, startTime, endTime)

        val currentTime = System.currentTimeMillis()

        val usageEvents = usageStatsManager.queryEvents(currentTime - 1000, System.currentTimeMillis())
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                val usageStat = usageStatsList.filter { usage -> usage.packageName == event.packageName }
                return AppUsage(
                    packageName = event.packageName,
                    screenTime = usageStat[0].totalTimeInForeground
                )
            }
        }

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val blockedApps= SharedPreferencesManager.getBlockedApps(sharedPreferences)

        val notification = NotificationCompat.Builder(this, "app_locker_channel")
            .setContentTitle("App Blocker")
            .setContentText("Blocking Selected Apps")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        monitorRunningApp(blockedApps)

        return START_STICKY
    }

    private fun monitorRunningApp(blockedApps: List<UserApps>) {
        runnable = Runnable {
            val currentApp = getCurrentRunningAppPackageName(applicationContext)
//            Log.w("APP LOCK SERVICE APPS", blockedApps.toString())
            if (currentApp != null) {
                val blockedApp = blockedApps.filter { app -> app.packageName == currentApp.packageName }
//                Log.w("APP LOCK SERVICE BLOCK", blockedApp.toString())
//                Log.w("APP LOCK SERVICE CURR", currentApp.toString())

                if (blockedApp.isNotEmpty()) {
//                    Log.w("SCREEN TIME", "${blockedApp[0].limit} ${currentApp.screenTime}")
                    if (currentApp.screenTime >= blockedApp[0].limit || blockedApp[0].limit == 0L) {
//                        Log.w("APP LOCK SERVICE CURR", "Current Running App Package Name: ${currentApp.packageName}")
                        val intent = Intent(applicationContext, BlockActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        applicationContext.startActivity(intent)
                    }
                }
            } else {
//                Log.w("APP LOCK SERVICE","No running app found.")
            }

            monitorRunningApp(blockedApps)
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