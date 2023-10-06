package com.example.parental_control_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.screens.BlockedAppsScreen
import com.example.parental_control_app.viewmodels.BlockedAppsViewModel

class BlockedAppsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val blockedAppsViewModel = BlockedAppsViewModel(kidProfileId.toString())

        setContent {
            BlockedAppsScreen(blockedAppsViewModel) {
                finish()
            }
        }
    }
}