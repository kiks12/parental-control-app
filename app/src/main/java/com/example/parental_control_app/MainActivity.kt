package com.example.parental_control_app

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.parental_control_app.activities.LoginActivity


class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_SMS = 1001
        private const val PERMISSION_REQUEST_LOCATION = 1002
        private const val PERMISSION_REQUEST_OVERLAY = 1003
        private const val PERMISSION_REQUEST_USAGE = 1004
    }

    private fun isUsagePermissionGranted(): Boolean {
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(
                packageName, 0
            )
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            var mode = 0
            mode = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid, applicationInfo.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.areNotificationsEnabled()
        } else {
            true
        }
    }

    private fun isOverlayPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true // Overlay permission is automatically granted before Android M
        if (!Settings.canDrawOverlays(this)) return false
        return false
    }

    private fun requestPermission(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestCode,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // CREATING A NOTIFICATION CHANNEL
        val name = "app_locker"
        val descriptionText = "channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("app_locker_channel", name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
        // CREATING A NOTIFICATION CHANNEL



        // REQUEST PERMISSION ON OVERLAY
        if (isOverlayPermissionGranted().not()) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, mChannel.id)
            }
            startActivity(intent)
//            requestPermission(
//                arrayOf(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),
//                PERMISSION_REQUEST_OVERLAY
//            )
        }
        // REQUEST PERMISSION ON OVERLAY


        // REQUEST PERMISSION ON APP USAGE
        if (isUsagePermissionGranted().not()) {
//            requestPermission(
//                arrayOf(Settings.ACTION_USAGE_ACCESS_SETTINGS),
//                PERMISSION_REQUEST_USAGE
//            )
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, mChannel.id)
            }
            startActivity(intent)
        }

        if (isNotificationPermissionGranted().not()) {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            } else {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }

        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
                PERMISSION_REQUEST_SMS
            )
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_LOCATION)
        }


        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
        finish()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // HANDLE REQUEST PERMISSION RESULTS
        when (requestCode) {
            PERMISSION_REQUEST_OVERLAY -> {

            }
        }

    }

}