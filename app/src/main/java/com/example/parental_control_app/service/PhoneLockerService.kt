package com.example.parental_control_app.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.parental_control_app.R
import com.example.parental_control_app.activities.stacking.LockActivity
import com.example.parental_control_app.activities.stacking.UnlockActivity
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PhoneLockerService : Service() {

    private val usersRepository = UsersRepository()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable {}
    private val checkInterval = 600
    private val db = Firebase.firestore
    private var isLocked = false
    private var shouldLock = false
    private var phoneLock = false
    private var screenTimeLimit = 0L

    @OptIn(DelicateCoroutinesApi::class)
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

        val documentRef = uid.let { db.collection("profiles").document(it!!) }
        documentRef.addSnapshotListener{ snapshot, _ ->
            val profile = snapshot?.toObject(UserProfile::class.java)

            if (profile != null) {
                shouldLock = profile.phoneLock
                phoneLock = profile.phoneLock
                screenTimeLimit = profile.phoneScreenTimeLimit

                if (profile.uninstalled) {
                    GlobalScope.launch {
                        usersRepository.saveUninstalledStatus(uid.toString(), false)
                    }
                }

                if (!profile.activeStatus) {
                    GlobalScope.launch {
                        usersRepository.saveProfileStatus(uid.toString(), true)
                    }
                }

                if (!shouldLock && isLocked) {
                    val unlockIntent = Intent(applicationContext, UnlockActivity::class.java)
                    unlockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    applicationContext.startActivity(unlockIntent)
                }
            }
        }

        monitorLocker()

        return START_STICKY
    }

    private fun lockPhone() {
        isLocked = true
        val intent = Intent(applicationContext, LockActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("time", screenTimeLimit)
        applicationContext.startActivity(intent)
    }

    private fun monitorLocker() {
        runnable = Runnable {
            if (shouldLock) lockPhone()
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