package com.example.parental_control_app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.graphics.drawable.toBitmap
import com.example.parental_control_app.data.UserAppIcon
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.repositories.AppsRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppSaverService @Inject constructor(private val appsRepository: AppsRepository = AppsRepository()): Service(){

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val profileId = intent?.getStringExtra("profileId").toString()
        val appList = mutableListOf<UserApps>()
        val iconList = mutableListOf<UserAppIcon>()
        val packages = packageManager.getInstalledApplications(0)

        appList.clear()
        packages.forEach list@{
            if (packageManager.getLaunchIntentForPackage(it.packageName!!) == null) return@list
            val gson = Gson()
            appList.add(UserApps(
                name = it.packageName!!,
                icon = gson.toJson(packageManager.getApplicationIcon(it)),
            ))
            iconList.add(UserAppIcon(
                name = it.packageName!!,
                icon = packageManager.getApplicationIcon(it).toBitmap()
            ))
        }

        scope.launch {
            async { appsRepository.saveApps(profileId,appList) }.await()
            async { appsRepository.saveAppIcons(profileId, iconList) }.await()
        }

        return START_STICKY
    }

}