package com.example.parental_control_app.activities.sms

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.screens.sms.SmsScreen
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.sms.SmsViewModel

class SmsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, Context.MODE_PRIVATE)
        val activityStarterHelper = ActivityStarterHelper(this)
        val profile = SharedPreferencesManager.getProfile(sharedPreferences)
        val kidProfileId = intent.getStringExtra("kidProfileId").toString()
        val smsViewModel = SmsViewModel(profile!!, kidProfileId)
        smsViewModel.setActivityStarterHelper(activityStarterHelper)

        setContent {
            ParentalControlAppTheme {
                SmsScreen(smsViewModel) {
                    finish()
                }
            }
        }
    }
}