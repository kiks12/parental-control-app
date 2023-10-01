package com.example.parental_control_app.activities.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.screens.notifications.NotificationPackageScreen
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.notifications.NotificationPackageViewModel

class NotificationPackageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val packageName = intent.getStringExtra("packageName")
        val notificationPackageViewModel = NotificationPackageViewModel(kidProfileId!!, packageName!!)
        notificationPackageViewModel.addOnBackClick { finish() }

        setContent {
            ParentalControlAppTheme {
                NotificationPackageScreen(notificationPackageViewModel)
            }
        }
    }
}