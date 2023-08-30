package com.example.parental_control_app.activities.sms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.screens.sms.SmsScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.sms.SmsViewModel

class SmsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityStarterHelper = ActivityStarterHelper(this)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val smsViewModel = SmsViewModel(kidProfileId!!)
        smsViewModel.addOnBackClick { finish() }
        smsViewModel.setActivityStarterHelper(activityStarterHelper)

        setContent {
            ParentalcontrolappTheme {
                SmsScreen(smsViewModel)
            }
        }
    }
}