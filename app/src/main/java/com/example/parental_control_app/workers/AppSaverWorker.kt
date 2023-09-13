package com.example.parental_control_app.workers

import android.content.Context
import android.util.Log
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parental_control_app.activities.children.ChildrenAppsActivity
import com.example.parental_control_app.data.UserAppIcon
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.repositories.AppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AppSaverWorker(private val ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params){

    private val appsRepository = AppsRepository()

    override suspend fun doWork(): Result {

        val profileId = inputData.getString(ChildrenAppsActivity.APP_PROFILE_ID_KEY).toString()
        val appList = mutableListOf<UserApps>()
        val iconList = mutableListOf<UserAppIcon>()
        val installedApplications = ctx.packageManager.getInstalledApplications(0)

        installedApplications.forEach { app ->
            if (ctx.packageManager.getLaunchIntentForPackage(app.packageName!!) == null) return@forEach
            val icon = ctx.packageManager.getApplicationIcon(app)
            Log.w("APP SAVER WORKER", icon.toString())
            appList.add(
                UserApps(
                    label = ctx.packageManager.getApplicationLabel(app).toString(),
                    packageName = app.packageName,
                )
            )
            iconList.add(
                UserAppIcon(
                    name = app.packageName,
                    icon = icon.toBitmapOrNull()
                )
            )
        }

        Log.w("APP SAVER WORKER", appList.toString())
        Log.w("APP SAVER WORKER", iconList.toString())
        Log.w("APP SAVER WORKER", profileId)

        coroutineScope{
            launch(Dispatchers.IO) {
                async {
                    Log.w("APP SAVER WORKER", "Saving User Apps")
                    appsRepository.saveApps(profileId, appList)
                }.await()
                async {
                    Log.w("APP SAVER WORKER", "Saving User App Icons")
                    appsRepository.saveAppIcons(profileId, iconList)
                }.await()
            }
        }

        return Result.success()
    }

}