package com.example.parental_control_app.activities.children

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.parental_control_app.screens.children.ChildrenScreen
import com.example.parental_control_app.viewmodels.children.ChildrenViewModel
import com.example.parental_control_app.helpers.SignOutHelper
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.service.AppLockerService
import com.example.parental_control_app.service.AppSaverService
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.workers.ScreenTimeGetterWorker
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ChildrenMainActivity : AppCompatActivity() {

    private lateinit var signOutHelper: SignOutHelper
    private lateinit var sharedPreferences: SharedPreferences
    private val appsRepository = AppsRepository()
    private val usersRepository = UsersRepository()

    companion object {
        const val BLOCKED_APPS_KEY = "BLOCKED_APPS_KEY"
    }

    private fun isUsageStatsPermissionGranted(): Boolean {
        val appOps = applicationContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            applicationContext.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun openUsageAccessSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, Context.MODE_PRIVATE)
        signOutHelper = SignOutHelper(this, sharedPreferences)

        val profile = SharedPreferencesHelper.getProfile(sharedPreferences)

        val activityStarterHelper = ActivityStarterHelper(this)
        val childrenViewModel = ChildrenViewModel(activityStarterHelper)
        childrenViewModel.setSignOutFunction { signOutHelper.signOut() }
        childrenViewModel.setProfile(profile!!)

        if (isUsageStatsPermissionGranted().not()) {
            openUsageAccessSettings(applicationContext)
        }

        startService(Intent(this, AppSaverService::class.java).also {
            it.putExtra("profileId", profile.profileId)
        })

        val screenTimeGetterWorkRequest: WorkRequest = PeriodicWorkRequest.Builder(
            ScreenTimeGetterWorker::class.java, 15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(screenTimeGetterWorkRequest)

        GlobalScope.launch(Dispatchers.IO) {
            var uid = ""
            var list = listOf<String>()
            async { uid = usersRepository.getProfileUID(profile.profileId) }.await()
            async { list = appsRepository.getBlockedAppNames(uid) }.await()
            async {
                startForegroundService(
                    Intent(applicationContext, AppLockerService::class.java)
                        .putExtra(BLOCKED_APPS_KEY, list.toTypedArray())
                )
            }.await()
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
}