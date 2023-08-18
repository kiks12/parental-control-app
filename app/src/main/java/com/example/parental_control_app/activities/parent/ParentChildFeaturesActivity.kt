package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.screens.parent.ParentChildFeaturesScreen
import com.example.parental_control_app.viewmodels.parent.ParentChildFeaturesViewModel

class ParentChildFeaturesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, MODE_PRIVATE)
        val profile = SharedPreferencesHelper.getProfile(sharedPreferences)
        val kidProfileId = intent.getStringExtra("profileId")

        val activityStarterHelper = ActivityStarterHelper(this)

        val parentChildFeaturesViewModel = ParentChildFeaturesViewModel()

        parentChildFeaturesViewModel.setProfileId(profile?.profileId!!)
        parentChildFeaturesViewModel.setKidProfileId(kidProfileId.toString())
        parentChildFeaturesViewModel.addOnBackClick { finish() }
        parentChildFeaturesViewModel.setActivityStartHelper(activityStarterHelper)

        setContent {
            ParentChildFeaturesScreen(parentChildFeaturesViewModel)
        }
    }
}