package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.screens.parent.ParentChildAppsScreen
import com.example.parental_control_app.viewmodels.parent.ParentChildAppsViewModel

class ParentChildAppsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, MODE_PRIVATE)
//        val profile = SharedPreferencesHelper.getProfile(sharedPreferences)
        val kidProfileId = intent.getStringExtra("kidProfileId")
        val parentChildAppsViewModel = ParentChildAppsViewModel(kidProfileId.toString())
        parentChildAppsViewModel.addOnBackClick { finish() }

        setContent {
            ParentChildAppsScreen(parentChildAppsViewModel)
        }
    }
}