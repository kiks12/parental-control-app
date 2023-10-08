package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.screens.parent.ParentChildAppsScreen
import com.example.parental_control_app.viewmodels.parent.ParentChildAppsViewModel

class ParentChildAppsActivity : AppCompatActivity() {

    private lateinit var parentChildAppsViewModel : ParentChildAppsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val profile = SharedPreferencesManager.getProfile(sharedPreferences)
        val kidProfileId = intent.getStringExtra("kidProfileId")

        if (profile != null) {
            parentChildAppsViewModel = ParentChildAppsViewModel(profile, kidProfileId.toString())
            parentChildAppsViewModel.addOnBackClick { finish() }
        }

        setContent {
            ParentChildAppsScreen(parentChildAppsViewModel)
        }
    }
}