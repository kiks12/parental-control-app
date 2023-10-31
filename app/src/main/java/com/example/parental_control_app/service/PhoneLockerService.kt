package com.example.parental_control_app.service

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.parental_control_app.R
import com.example.parental_control_app.activities.stacking.LockActivity
import com.example.parental_control_app.activities.stacking.UnlockActivity
import com.example.parental_control_app.data.AppUsage
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.users.UserProfile
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class PhoneLockerService : Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable {}
    private val checkInterval = 900
    private val db = Firebase.firestore
    private var isLocked = false
    private var shouldLock = false
    private var phoneLock = false
    private var previousApp = ""
    private var previous = false
    private var screenTimeLimit = 0L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val uid = SharedPreferencesManager.getUID(sharedPreferences)

        val notification = NotificationCompat.Builder(this, "Phone Locker")
            .setContentTitle("Phone Locker")
            .setContentText("Your parent might lock your phone anytime")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        scope.launch {
            val data = db.collection("profiles").document(uid!!).get().await().data
            screenTimeLimit = data?.get("phoneScreenTimeLimit").toString().toLong()
        }

        monitorLocker()

        val documentRef = uid.let { db.collection("profiles").document(it!!) }
        documentRef.addSnapshotListener{ snapshot, _ ->
            val profile = snapshot?.toObject(UserProfile::class.java)

            if (profile != null) {
                shouldLock = profile.phoneLock
                phoneLock = profile.phoneLock

                if (!shouldLock && isLocked && previous != profile.phoneLock) {
                    val unlockIntent = Intent(applicationContext, UnlockActivity::class.java)
                    unlockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    applicationContext.startActivity(unlockIntent)
                }

                isLocked = false
                screenTimeLimit = profile.phoneScreenTimeLimit
                previous = profile.phoneLock
            }
        }

        return START_STICKY
    }

    private fun getCurrentRunningAppPackageName(context: Context) : Map<String, Any?> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        var appUsage : AppUsage? = null

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
                appUsage = AppUsage(
                    packageName = event.packageName,
                    screenTime = usageStat[0].totalTimeInForeground
                )
            }
        }

        val totalScreenTime = usageStatsList.sumOf { stats -> stats.totalTimeInForeground }

        return mapOf(
            "APP_USAGE" to appUsage,
            "TOTAL_SCREEN_TIME" to totalScreenTime
        )
    }

    private fun lockPhone() {
        val intent = Intent(applicationContext, LockActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
        isLocked = true
    }

    private fun monitorLocker() {
        runnable = Runnable {
            if (shouldLock && !isLocked) lockPhone()

            val usageStats = getCurrentRunningAppPackageName(applicationContext)

            val currentApp = usageStats["APP_USAGE"] as AppUsage?
            val totalScreenTime = usageStats["TOTAL_SCREEN_TIME"].toString().toLong()

//            Log.w("PHONE LOCKER SERVICE", currentApp.toString())
//            Log.w("PHONE LOCKER SERVICE", totalScreenTime.toString())
//            Log.w("PHONE LOCKER SERVICE", screenTimeLimit.toString())
//            Log.w("PHONE LOCKER SERVICE", phoneLock.toString())

            if (currentApp != null && currentApp.packageName != previousApp && currentApp.packageName != application.packageName) {
                isLocked = false
            }

            shouldLock = (totalScreenTime >= screenTimeLimit && screenTimeLimit != 0L && currentApp?.packageName != previousApp) || phoneLock

            if (currentApp != null) {
                previousApp = currentApp.packageName
            }

            monitorLocker()
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