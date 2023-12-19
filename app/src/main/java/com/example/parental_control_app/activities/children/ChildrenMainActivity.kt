package com.example.parental_control_app.activities.children

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.example.parental_control_app.screens.children.ChildrenScreen
import com.example.parental_control_app.viewmodels.children.ChildrenViewModel
import com.example.parental_control_app.helpers.ProfileSignOutHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.broadcast.receivers.InternetConnectivityReceiver
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.service.AppBlockerService
import com.example.parental_control_app.service.PhoneLockerService
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.SettingsType
import com.example.parental_control_app.viewmodels.SettingsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ChildrenMainActivity : AppCompatActivity() {

    private lateinit var profileSignOutHelper: ProfileSignOutHelper
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profile : UserProfile
    private val toastHelper = ToastHelper(this)
    private val appsRepository = AppsRepository()
    private val usersRepository = UsersRepository()
    private val granted = mutableStateOf(false)

    companion object {
        const val NOTIFICATION_PERMISSION_CODE = 10

        private const val APP_BLOCKER_NAME = "App Blocker"
        private const val APP_BLOCKER_DESCRIPTION = "ENTER APP BLOCKER DESCRIPTION"
        private const val PHONE_LOCKER_NAME = "Phone Locker"
        private const val PHONE_LOCKER_DESCRIPTION = "ENTER PHONE LOCKER DESCRIPTION"
    }

    private fun isNotificationPermissionGranted() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return NotificationManagerCompat.from(this).areNotificationsEnabled()
        }

        return false
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    private fun startAppLockerForegroundService() {
        lifecycleScope.launch {
            async { toastHelper.makeToast("Starting App Blocker Worker") }.await()

            if (isOnline(this@ChildrenMainActivity)) {
                val uid = usersRepository.getProfileUID(profile.profileId)
                val list = appsRepository.getBlockedApps(uid)

                SharedPreferencesManager.storeBlockedApps(sharedPreferences, list)
            }

            async {
                // Stopping Service to ensure only one service per category is working
                stopService(Intent(applicationContext, AppBlockerService::class.java))
                stopService(Intent(applicationContext, PhoneLockerService::class.java))
                startForegroundService(Intent(applicationContext, AppBlockerService::class.java))
                startForegroundService(Intent(applicationContext, PhoneLockerService::class.java))
            }.await()

            async { toastHelper.makeToast("App Blocker Worker Running") }.await()
        }
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val appBlockerChannel = NotificationChannel("App Blocker", APP_BLOCKER_NAME, importance)
        appBlockerChannel.description = APP_BLOCKER_DESCRIPTION

        val phoneLockerChannel = NotificationChannel("Phone Locker", PHONE_LOCKER_NAME, importance)
        phoneLockerChannel.description = PHONE_LOCKER_DESCRIPTION

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(appBlockerChannel)
        notificationManager.createNotificationChannel(phoneLockerChannel)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
            return
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:$packageName"))
            startActivity(intent)
            return
        }
    }

    private fun saveDeviceModel() {
        lifecycleScope.launch {
            if (isOnline(this@ChildrenMainActivity)) {
                val uid = usersRepository.getProfileUID(profile.profileId)
                val deviceName = Build.MANUFACTURER + "-" + Build.MODEL
                val response = usersRepository.saveDeviceModel(uid, deviceName)
                toastHelper.makeToast(response?.message!!)
                val editor = sharedPreferences.edit()
                editor.putBoolean(SharedPreferencesManager.DEVICE_NAME_SAVED_KEY, true)
                editor.apply()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, Context.MODE_PRIVATE)
        profileSignOutHelper = ProfileSignOutHelper(this, sharedPreferences)
        profile = SharedPreferencesManager.getProfile(sharedPreferences)!!

        lifecycleScope.launch {
            val uid = usersRepository.getProfileUID(profile.profileId)
            usersRepository.saveProfileStatus(uid, true)
        }

        val deviceNameSaved = SharedPreferencesManager.isDeviceSaved(sharedPreferences)

        val activityStarterHelper = ActivityStarterHelper(this)
        val settingsViewModel = SettingsViewModel(SettingsType.CHILD)
        settingsViewModel.signOut = {
            WorkManager.getInstance(applicationContext).cancelAllWork()
            profileSignOutHelper.signOut()
        }
        val childrenViewModel = ChildrenViewModel(settingsViewModel, activityStarterHelper)

        childrenViewModel.setProfile(profile)

        when (isNotificationPermissionGranted()) {
            true -> {
                createNotificationChannel()
                startAppLockerForegroundService()
                granted.value = true
            }
            else -> {}
        }

        when (deviceNameSaved) {
            true -> {}
            false -> { saveDeviceModel() }
        }

        val internetConnectivityReceiver = InternetConnectivityReceiver(this, profile.profileId)
        internetConnectivityReceiver.register()

        setContent {

            val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

            ParentalControlAppTheme {
                Surface (
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        PermissionRequired(
                            permissionState = permissionState,
                            permissionNotGrantedContent = {
                                Column(
                                    modifier= Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Permission Required", fontSize=30.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                    Text("Post notification is required to use the app, this is to enable blocking and locking features", textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(50.dp))
                                    Button(onClick = { permissionState.launchPermissionRequest() } ) {
                                        Text("Allow Notification")
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text("After allowing notification permission, restart the app", textAlign = TextAlign.Center)
                                }
                            },
                            permissionNotAvailableContent = {
                                Column(
                                    modifier= Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Permission Required", fontSize=30.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                    Text("Post notification is required to use the app, this is to enable blocking and locking features", textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(50.dp))
                                    Button(onClick = { permissionState.launchPermissionRequest() } ) {
                                        Text("Allow Notification")
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text("After allowing notification permission, restart the app", textAlign = TextAlign.Center)
                                }
                            }
                        ) {
                            ChildrenScreen(childrenViewModel)
                        }
                    } else {
                        if (granted.value) {
                            ChildrenScreen(childrenViewModel)
                        } else {
                            Column(
                                modifier= Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Permission Required", fontSize=30.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                Text("Post notification is required to use the app, this is to enable blocking and locking features", textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(50.dp))
                                Button(onClick = ::requestNotificationPermission ) {
                                    Text("Allow Notification")
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Text("After allowing notification permission, restart the app", textAlign = TextAlign.Center)
                            }
                        }
                    }
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
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startAppLockerForegroundService()
                    }
                }
            }
            else -> {}
        }
    }
}