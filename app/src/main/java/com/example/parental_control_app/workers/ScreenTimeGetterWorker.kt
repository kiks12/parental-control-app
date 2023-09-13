package com.example.parental_control_app.workers

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parental_control_app.activities.children.ChildrenAppsActivity
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.Calendar

class ScreenTimeGetterWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val appsRepository = AppsRepository()
    private val usersRepository = UsersRepository()
    private var uid = ""

    private suspend fun fetchAppNames() : List<String> {
        val profileId = inputData.getString(ChildrenAppsActivity.APP_PROFILE_ID_KEY).toString()
        val completable = CompletableDeferred<List<String>>()

        coroutineScope {
            async { uid = usersRepository.getProfileUID(profileId) }.await()
            async {
                completable.complete(appsRepository.getAppNames(uid))
            }.await()
        }

        return completable.await()
    }

    override suspend fun doWork(): Result {
        val usageStatsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
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


        var appNames = listOf<String>()
        var appListUpdated = listOf<UserApps>()
        coroutineScope {
            async { appNames = fetchAppNames() }.await()
            async {
                for (usageStats in usageStatsList) {
                    if (appNames.contains(usageStats.packageName)) {
                        val appData = UserApps(
                            packageName = usageStats.packageName,
                            screenTime = usageStats.totalTimeInForeground
                        )
                        appListUpdated = appListUpdated.plus(appData)
                    }
                }
            }.await()
            async { appsRepository.updateAppScreenTime(uid, appListUpdated)}.await()
        }

        return Result.success()
    }
}