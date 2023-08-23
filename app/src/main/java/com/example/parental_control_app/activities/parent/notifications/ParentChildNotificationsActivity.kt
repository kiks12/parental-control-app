package com.example.parental_control_app.activities.parent.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.screens.parent.notifications.ParentChildNotificationsScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.parent.notifications.ParentChildNotificationsViewModel

class ParentChildNotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityStarterHelper = ActivityStarterHelper(this)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val parentChildNotificationsViewModel = ParentChildNotificationsViewModel(kidProfileId!!)
        parentChildNotificationsViewModel.addOnBackClick { finish() }
        parentChildNotificationsViewModel.setActivityStarterHelper(activityStarterHelper)

        setContent {
            ParentalcontrolappTheme {
                ParentChildNotificationsScreen(parentChildNotificationsViewModel)
            }
        }
    }
}