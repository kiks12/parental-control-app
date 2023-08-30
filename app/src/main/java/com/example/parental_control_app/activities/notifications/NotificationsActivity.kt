package com.example.parental_control_app.activities.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.screens.notifications.NotificationsScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.notifications.NotificationsViewModel

class NotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityStarterHelper = ActivityStarterHelper(this)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val notificationsViewModel = NotificationsViewModel(kidProfileId!!)
        notificationsViewModel.addOnBackClick { finish() }
        notificationsViewModel.setActivityStarterHelper(activityStarterHelper)

        setContent {
            ParentalcontrolappTheme {
                NotificationsScreen(notificationsViewModel)
            }
        }
    }
}