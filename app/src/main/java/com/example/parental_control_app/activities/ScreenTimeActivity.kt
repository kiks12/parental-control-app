package com.example.parental_control_app.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.screens.ScreenTimeScreen
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.ScreenTimeViewModel

class ScreenTimeActivity : AppCompatActivity() {

    private lateinit var screenTimeViewModel : ScreenTimeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val profile = SharedPreferencesManager.getProfile(sharedPreferences)

        val kidProfileId = intent.getStringExtra("kidProfileId")

        if (profile != null) {
            screenTimeViewModel = ScreenTimeViewModel(profile, kidProfileId!!)
        }

        setContent {
            ParentalControlAppTheme {
                ScreenTimeScreen(screenTimeViewModel) {
                    finish()
                }
            }
        }
    }
}