package com.example.parental_control_app.workers

import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.repositories.AppsRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.Calendar

class ScreenTimeGetterWorker(private val ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val appsRepository = AppsRepository()
    private var uid = ""

    private suspend fun fetchAppNames() : List<String> {
        val sharedPreferences = applicationContext.getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, 0)
        val profile = SharedPreferencesHelper.getProfile(sharedPreferences)
        val completable = CompletableDeferred<List<String>>()

        coroutineScope {
            async { uid = appsRepository.getProfileUID(profile?.profileId!!) }.await()
            async {
                completable.complete(appsRepository.getAppNames(uid))
            }.await()
        }

        return completable.await()
    }
    override suspend fun doWork(): Result {
        val usageStatsManager = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val interval = UsageStatsManager.INTERVAL_DAILY
        val usageStatsList = usageStatsManager.queryUsageStats(interval, startTime, endTime)


        var appNames = listOf<String>()
        var appListUpdated = listOf<UserApps>()
        coroutineScope {
            async { appNames = fetchAppNames() }.await()
            async {
                Log.w("APP USAGE NAMES", appNames.toString())
                for (usageStats in usageStatsList) {
                    if (appNames.contains(usageStats.packageName)) {
                        val appData = UserApps(
                            name = usageStats.packageName,
                            icon = "",
                            screenTime = usageStats.totalTimeInForeground
                        )
                        Log.w("APP USAGE", appData.toString())
                        appListUpdated = appListUpdated.plus(appData)
                    }
                }
            }.await()
            async { Log.w("APP USAGE", appListUpdated.toString()) }.await()
            async { appsRepository.updateAppScreenTime(uid, appListUpdated)}.await()
        }

        return Result.success()
    }
}