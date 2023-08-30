package com.example.parental_control_app.activities.sms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.screens.sms.SmsMessageScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.sms.SmsMessageViewModel

class SmsMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val sender = intent.getStringExtra("sender")
        val smsMessageViewModel = SmsMessageViewModel(kidProfileId!!, sender!!)
        smsMessageViewModel.addOnBackClick{ finish() }

        setContent {
            ParentalcontrolappTheme {
                SmsMessageScreen(smsMessageViewModel)
            }
        }
    }
}