package com.example.parental_control_app.workers

import android.content.Context
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parental_control_app.activities.children.ChildrenAppsActivity
import com.example.parental_control_app.data.UserAppIcon
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.coroutineScope

class AppSaverWorker(private val ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params){

    private val appsRepository = AppsRepository()
    private val usersRepository = UsersRepository()

    override suspend fun doWork(): Result {

        val profileId = inputData.getString(ChildrenAppsActivity.APP_PROFILE_ID_KEY).toString()
        val appList = mutableListOf<UserApps>()
        val iconList = mutableListOf<UserAppIcon>()
        val installedApplications = ctx.packageManager.getInstalledApplications(0)

        installedApplications.forEach { app ->
            if (ctx.packageManager.getLaunchIntentForPackage(app.packageName!!) == null) return@forEach
            val icon = ctx.packageManager.getApplicationIcon(app)


            /*
             * UNCOMMENT FOR DEBUGGING
             */
//            Log.w("APP SAVER WORKER", icon.toString())


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


        /*
         * UNCOMMENT FOR DEBUGGING PURPOSES
         */
//        Log.w("APP SAVER WORKER", appList.toString())
//        Log.w("APP SAVER WORKER", iconList.toString())
//        Log.w("APP SAVER WORKER", profileId)

        coroutineScope {
            val uid = usersRepository.getProfileUID(profileId)
            // UNCOMMENT WHEN DEBUGGING
            // Log.w("APP SAVER WORKER", "Saving User Apps")
            appsRepository.saveApps(uid, appList)
//                Toast.makeText(applicationContext, appsResponse, Toast.LENGTH_SHORT).show()
            // UNCOMMENT WHEN DEBUGGING
            // Log.w("APP SAVER WORKER", "Saving User App Icons")
            appsRepository.saveAppIcons(uid, iconList)
//                Toast.makeText(applicationContext, appIconResponse, Toast.LENGTH_SHORT).show()
        }

        return Result.success()
    }

}