package com.example.parental_control_app.activities.notifications

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.screens.notifications.NotificationsScreen
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.notifications.NotificationsViewModel


class NotificationsActivity : AppCompatActivity() {

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val profile = SharedPreferencesManager.getProfile(sharedPreferences)

        val activityStarterHelper = ActivityStarterHelper(this)
        val kidProfileId = intent.getStringExtra("kidProfileId")
        val notificationsViewModel = NotificationsViewModel(kidProfileId!!, activityStarterHelper)

        val cn = ComponentName(this, NotificationListenerService::class.java)
        val flat: String = Settings.Secure.getString(
            this.contentResolver,
            "enabled_notification_listeners"
        )
        val enabled = flat.contains(cn.flattenToString())

        if (
            profile != null && profile.child &&
            !enabled
        ) {
            resultLauncher.launch(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }

        setContent {
            ParentalControlAppTheme {
                NotificationsScreen(notificationsViewModel) {
                    finish()
                }
            }
        }
    }
}