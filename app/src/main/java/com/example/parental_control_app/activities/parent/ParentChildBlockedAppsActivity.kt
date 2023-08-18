package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.screens.parent.ParentChildBlockedAppsScreen
import com.example.parental_control_app.viewmodels.parent.ParentChildBlockedAppsViewModel

class ParentChildBlockedAppsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val parentChildBlockedAppsViewModel = ParentChildBlockedAppsViewModel(kidProfileId.toString())
        parentChildBlockedAppsViewModel.addOnBackClick { finish() }

        setContent {
            ParentChildBlockedAppsScreen(parentChildBlockedAppsViewModel)
        }
    }
}