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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.parental_control_app.activities.LoginActivity


class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_SMS = 1001 // Define your request code here
    }

    private fun isUsagePermissionGranted(): Boolean {
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(
                packageName, 0
            )
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            var mode = 0
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName
                )
            }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = "app_locker"
        val descriptionText = "channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("app_locker_channel", name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        if (isOverlayPermissionGranted().not()) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, mChannel.id)
            }
            startActivity(intent)
        }

        if (isUsagePermissionGranted().not()) {
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
        } else {
            // Permissions are already granted
            // Register the BroadcastReceiver
        }


        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
        finish()
    }
}