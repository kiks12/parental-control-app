package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.R
import com.example.parental_control_app.screens.parent.ParentChildNotificationsScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.parent.ParentChildNotificationsViewModel

class ParentChildNotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parentChildNotificationsViewModel = ParentChildNotificationsViewModel()

        setContent {
            ParentalcontrolappTheme {
                ParentChildNotificationsScreen(parentChildNotificationsViewModel)
            }
        }
    }
}