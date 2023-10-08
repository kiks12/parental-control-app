package com.example.parental_control_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.screens.BlockedAppsScreen
import com.example.parental_control_app.viewmodels.BlockedAppsViewModel

class BlockedAppsActivity : AppCompatActivity() {

    private lateinit var blockedAppsViewModel : BlockedAppsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val profile = SharedPreferencesManager.getProfile(sharedPreferences)

        val kidProfileId = intent.getStringExtra("kidProfileId")

        if (profile != null) {
            blockedAppsViewModel = BlockedAppsViewModel(profile, kidProfileId.toString())
        }

        setContent {
            BlockedAppsScreen(blockedAppsViewModel) {
                finish()
            }
        }
    }
}