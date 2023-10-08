package com.example.parental_control_app.activities.notifications

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.screens.notifications.NotificationsScreen
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.notifications.NotificationsViewModel

class NotificationsActivity : AppCompatActivity() {

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityStarterHelper = ActivityStarterHelper(this)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val notificationsViewModel = NotificationsViewModel(kidProfileId!!)
        notificationsViewModel.addOnBackClick { finish() }
        notificationsViewModel.setActivityStarterHelper(activityStarterHelper)

        resultLauncher.launch(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))

        setContent {
            ParentalControlAppTheme {
                NotificationsScreen(notificationsViewModel)
            }
        }
    }
}