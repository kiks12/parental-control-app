package com.example.parental_control_app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.parental_control_app.repositories.AppsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
        val appList = mutableListOf<String>()
        val packages = packageManager.getInstalledApplications(0)

        appList.clear()
        packages.forEach list@{
            if (packageManager.getLaunchIntentForPackage(it.packageName!!) == null) return@list
            appList.add(it.packageName!!)
        }

        scope.launch {
            appsRepository.saveApps(profileId,appList)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}