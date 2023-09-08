package com.example.parental_control_app.activities.children

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.helpers.ProfileSignOutHelper
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.workers.AppSaverWorker
import com.example.parental_control_app.workers.ScreenTimeGetterWorker
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class ChildrenAppsActivity : AppCompatActivity() {

    private lateinit var profileSignOutHelper: ProfileSignOutHelper
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profile: UserProfile

    companion object {
        const val APP_PROFILE_ID_KEY = "APP_PROFILE_ID_KEY"
    }

    private fun isUsagePermissionGranted(): Boolean {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val mode = appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
            return mode == AppOpsManager.MODE_ALLOWED
        }

        return false
    }

    private fun enqueueScreenTimeGetterWorker() {
        val data = Data.Builder()
            .putString(APP_PROFILE_ID_KEY, profile.profileId)
            .build()
        val periodicScreenTimeGetterWorker = PeriodicWorkRequest.Builder(
            ScreenTimeGetterWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setInputData(data).build()

        WorkManager.getInstance(applicationContext).enqueue(periodicScreenTimeGetterWorker)
    }

    private fun enqueueAppSaverWorker() {
        val data = Data.Builder()
            .putString(APP_PROFILE_ID_KEY, profile.profileId)
            .build()
        val periodicAppSaverWorker = PeriodicWorkRequest.Builder(
            AppSaverWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setInputData(data).build()

        WorkManager.getInstance(applicationContext).enqueue(periodicAppSaverWorker)
    }

    private fun initializeUI() {
        val list = mutableListOf<Map<String, Any>>()
        val packages = packageManager.getInstalledApplications(0)

        packages.forEach {
            if (packageManager.getLaunchIntentForPackage(it.packageName!!) == null) return@forEach

            val icon = it.loadIcon(packageManager)

            if (icon != null) {
                val bitmap = icon.toBitmap()
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

                list.add(
                    mapOf(
                        "app" to it,
                        "icon" to bitmap.asImageBitmap()
                    )
                )
            }
        }

        setContent {
            val apps = remember { list }

            ParentalcontrolappTheme {
                Scaffold (
                    topBar = { TopBar(onBackClick = { finish() }) },
                ){ innerPadding ->
                    Surface (
                        modifier = Modifier.padding(innerPadding)
                    ){
                        if (apps.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ){
                                Text("No Apps to Show")
                            }
                        } else {
                            AppGrid(apps = apps)
                        }
                    }
                }
            }
        }
    }

    private fun enqueueWorkerAndInitializeUI() {
        enqueueAppSaverWorker()
        enqueueScreenTimeGetterWorker()
        initializeUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, Context.MODE_PRIVATE)
        profileSignOutHelper = ProfileSignOutHelper(this, sharedPreferences)
        profile = SharedPreferencesHelper.getProfile(sharedPreferences)!!

        when (isUsagePermissionGranted()) {
            true -> {
                Log.w("PERMISSION", "Usage Permission Granted")
                enqueueWorkerAndInitializeUI()
            }
            else -> {
                Log.w("PERMISSION", "Usage Permission Denied")
                val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) enqueueWorkerAndInitializeUI()
                }

                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

                resultLauncher.launch(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}



@Composable
private fun AppGrid(apps: List<Map<String, Any>>) {
    LazyColumn(
        modifier = Modifier.padding(20.dp),
    ) {
        items(items = apps) {
            Row (
                modifier = Modifier.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ){
                Box(modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)) {
                    if (it["icon"] != null) {
                        Image(
                            it["icon"] as ImageBitmap,
                            contentDescription = (it["app"] as ApplicationInfo).packageName,
                            contentScale = ContentScale.FillBounds,
                        )
                    }
                }
                Text(
                    (it["app"] as ApplicationInfo).packageName,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, "Go Back")
            }
        },
        title = { Text("Apps") }
    )
}
