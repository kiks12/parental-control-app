package com.example.parental_control_app.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.screens.ScreenTimeScreen
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.ScreenTimeViewModel

class ScreenTimeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val screenTimeViewModel = ScreenTimeViewModel(kidProfileId!!)

        setContent {
            ParentalControlAppTheme {
                ScreenTimeScreen(screenTimeViewModel) {
                    finish()
                }
            }
        }
    }
}