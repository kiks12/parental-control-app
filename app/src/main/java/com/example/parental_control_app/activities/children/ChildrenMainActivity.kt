package com.example.parental_control_app.activities.children

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.example.parental_control_app.screens.children.ChildrenScreen
import com.example.parental_control_app.viewmodels.children.ChildrenViewModel
import com.example.parental_control_app.helpers.ProfileSignOutHelper
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.service.AppLockerService
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ChildrenMainActivity : AppCompatActivity() {

    private lateinit var profileSignOutHelper: ProfileSignOutHelper
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profile : UserProfile
    private val toastHelper = ToastHelper(this)
    private val appsRepository = AppsRepository()
    private val usersRepository = UsersRepository()

    companion object {
        const val BLOCKED_APPS_KEY = "BLOCKED_APPS_KEY"
        const val NOTIFICATION_PERMISSION_CODE = 10
    }

    private fun isNotificationPermissionGranted() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }

        return true
    }

    private fun startAppLockerForegroundService() {
        lifecycleScope.launch {
            var uid = ""
            var list = listOf<String>()
            async { toastHelper.makeToast("Starting App Blocker Worker") }.await()
            async { uid = usersRepository.getProfileUID(profile.profileId) }.await()
            async { list = appsRepository.getBlockedAppNames(uid) }.await()
            async {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Stopping Service to ensure only one App locker service is working
                    stopService(Intent(applicationContext, AppLockerService::class.java))
                    startForegroundService(
                        Intent(applicationContext, AppLockerService::class.java)
                            .putExtra(BLOCKED_APPS_KEY, list.toTypedArray())
                    )
                }
            }.await()
            async { toastHelper.makeToast("App Blocker Worker Running") }.await()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "app_locker"
            val descriptionText = "channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("app_locker_channel", name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, Context.MODE_PRIVATE)
        profileSignOutHelper = ProfileSignOutHelper(this, sharedPreferences)
        profile = SharedPreferencesHelper.getProfile(sharedPreferences)!!

        val activityStarterHelper = ActivityStarterHelper(this)
        val childrenViewModel = ChildrenViewModel(activityStarterHelper)
        childrenViewModel.setSignOutFunction {
            stopService(Intent(applicationContext, AppLockerService::class.java))
            WorkManager.getInstance(applicationContext).cancelAllWork()
            profileSignOutHelper.signOut()
        }
        childrenViewModel.setProfile(profile)

        when (isNotificationPermissionGranted()) {
            true -> {
                createNotificationChannel()
                startAppLockerForegroundService()
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
                }
            }
        }

        setContent {
            ParentalcontrolappTheme {
                Surface (
                    modifier = Modifier.fillMaxSize()
                ) {
                    ChildrenScreen(childrenViewModel)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NOTIFICATION_PERMISSION_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startAppLockerForegroundService()
                }
            }
            else -> {}
        }
    }
}