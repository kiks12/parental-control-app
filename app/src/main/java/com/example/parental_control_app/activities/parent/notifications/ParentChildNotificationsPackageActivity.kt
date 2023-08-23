package com.example.parental_control_app.activities.parent.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.parental_control_app.screens.parent.notifications.ParentChildNotificationPackageScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.parent.notifications.ParentChildNotificationsPackageViewModel

class ParentChildNotificationsPackageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val packageName = intent.getStringExtra("packageName")
        val parentChildNotificationPackageViewModel = ParentChildNotificationsPackageViewModel(kidProfileId!!, packageName!!)

        setContent {
            ParentalcontrolappTheme {
                val notifications = parentChildNotificationPackageViewModel.notificationsState

                Scaffold {innerPadding ->
                    Surface (
                        modifier = Modifier.padding(innerPadding)
                    ){
                        ParentChildNotificationPackageScreen(notifications)
                    }
                }
            }
        }
    }
}