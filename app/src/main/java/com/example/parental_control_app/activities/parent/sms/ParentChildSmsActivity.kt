package com.example.parental_control_app.activities.parent.sms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.screens.parent.sms.ParentChildSmsScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.parent.sms.ParentChildSmsViewModel

class ParentChildSmsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityStarterHelper = ActivityStarterHelper(this)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val parentChildSmsViewModel = ParentChildSmsViewModel(kidProfileId!!)
        parentChildSmsViewModel.addOnBackClick { finish() }
        parentChildSmsViewModel.setActivityStarterHelper(activityStarterHelper)

        setContent {
            ParentalcontrolappTheme {
                ParentChildSmsScreen(parentChildSmsViewModel)
            }
        }
    }
}