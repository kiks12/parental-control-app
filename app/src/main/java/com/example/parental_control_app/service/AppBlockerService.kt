package com.example.parental_control_app.service

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.parental_control_app.R
import com.example.parental_control_app.activities.stacking.BlockActivity
import com.example.parental_control_app.data.ActivityLog
import com.example.parental_control_app.data.AppUsage
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.ActivityLogRepository
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar

class AppBlockerService : Service(){

    private lateinit var uid : String
    private var prevApp = ""
    private val db = Firebase.firestore
    private var blockedApps : List<UserApps> = listOf()

    companion object {
        private val activityLogRepository = ActivityLogRepository()
        private val appsRepository = AppsRepository()
        private val usersRepository = UsersRepository()
        private val handler = Handler(Looper.getMainLooper())
        private var runnable = Runnable {}
        private const val checkInterval = 500
        private val job = SupervisorJob()
        private val scope = CoroutineScope(Dispatchers.IO + job)
    }

    private fun getCurrentRunningAppPackageName(context: Context): AppUsage? {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

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

//        Log.w("APP LOCK SERVICE", event.packageName)

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                val usageStat = usageStatsList.filter { usage -> usage.packageName == event.packageName }
                return AppUsage(
                    packageName = event.packageName,
                    screenTime = usageStat[0].totalTimeInForeground,
                    app = applicationContext.packageManager.getApplicationInfo(event.packageName, 0)
                )
            }
        }

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        blockedApps = SharedPreferencesManager.getBlockedApps(sharedPreferences)
        uid = SharedPreferencesManager.getUID(sharedPreferences).toString()

        val notification = NotificationCompat.Builder(this, "App Blocker")
            .setContentTitle("App Blocker and Activity Logger")
            .setContentText("Blocking Selected Apps and logging activity")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        db.collection("profiles").document(uid).addSnapshotListener{ snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener
            val profileData = snapshot.toObject(UserProfile::class.java) ?: return@addSnapshotListener

            if (profileData.blockChange) {
                scope.launch {
                    blockedApps = appsRepository.getBlockedApps(uid)
//                    Log.w("GET NEW BLOCKED APPS", "GET")
                    usersRepository.updateBlockStatus(uid, false)
                    SharedPreferencesManager.storeBlockedApps(sharedPreferences, blockedApps)
                }
            }
        }

        monitorRunningApp()

        return START_STICKY
    }

    private fun filterBlockedApp(blockedApps: List<UserApps>, packageName: String) : List<UserApps> {
        return blockedApps.filter { app -> app.packageName == packageName }
    }

    private fun showBlockActivity() {
//        Log.w("APP LOCK SERVICE CURR", "Current Running App Package Name: ${currentApp.packageName}")
        val intent = Intent(applicationContext, BlockActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }

    private fun logActivity(packageName: String, app: ApplicationInfo) {
        prevApp = packageName
        val newActivityLog = ActivityLog(
            label = applicationContext.packageManager.getApplicationLabel(app).toString(),
            packageName = packageName,
            datetime = Timestamp.now()
        )
        scope.launch {
            activityLogRepository.addActivityLog(uid, newActivityLog)
        }
    }

    private fun monitorRunningApp() {
        runnable = Runnable {
            Log.w("APP LOCK SERVICE", "RUN")
//            Log.w("BLOCKED APPS", blockedApps.toString())
            val currentApp = getCurrentRunningAppPackageName(applicationContext)
            Log.w("APP LOCK SERVICE", (currentApp != null).toString())
//            Log.w("APP LOCK SERVICE APPS", blockedApps.toString())
            if (currentApp != null) {

                if (currentApp.packageName != prevApp && currentApp.packageName != applicationInfo.packageName) {
                    logActivity(currentApp.packageName, currentApp.app!!)
                }

                val blockedApp = filterBlockedApp(blockedApps, currentApp.packageName)
//                Log.w("APP LOCK SERVICE BLOCK", blockedApp.toString())
//                Log.w("APP LOCK SERVICE CURR", currentApp.toString())

                if (blockedApp.isNotEmpty()) {
//                    Log.w("SCREEN TIME", "${blockedApp[0].limit} ${currentApp.screenTime}")
                    if (currentApp.screenTime >= blockedApp[0].limit || blockedApp[0].limit == 0L && blockedApp[0].packageName != prevApp) {
//                        Log.w("BLOCK APP", "$blockedApp")
                        showBlockActivity()
                    }
                }
            } else {
//                Log.w("APP LOCK SERVICE","No running app found.")
            }

            monitorRunningApp()
        }

        handler.postDelayed(runnable, checkInterval.toLong())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}