package com.example.parental_control_app.workers

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.parental_control_app.activities.LockActivity


class AppLockWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters){

    private fun isUsageStatsPermissionGranted(): Boolean {
        val appOps = applicationContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            applicationContext.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun openUsageAccessSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun getCurrentRunningAppPackageName(context: Context): String? {
        val currentTime = System.currentTimeMillis()
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageEvents = usageStatsManager.queryEvents(currentTime - 10000, currentTime)

        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                return event.packageName
            }
        }

        return null
    }

    override suspend fun doWork(): Result {

        if (!isUsageStatsPermissionGranted()) {
            openUsageAccessSettings(applicationContext)
        } else {
            val currentAppPackageName = getCurrentRunningAppPackageName(applicationContext)
            if (currentAppPackageName != null) {
                if (currentAppPackageName.contains("youtube")) {
                    Log.w("APP LOCK WORKER", "Current Running App Package Name: $currentAppPackageName")
                    val intent = Intent(applicationContext, LockActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    applicationContext.startActivity(intent)
                }
            } else {
                Log.w("APP LOCK WORKER","No running app found.")
            }
        }

        val workRequest = OneTimeWorkRequestBuilder<AppLockWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
        return Result.success()
    }

}