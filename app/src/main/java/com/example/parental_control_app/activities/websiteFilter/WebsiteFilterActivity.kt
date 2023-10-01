package com.example.parental_control_app.activities.websiteFilter

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.screens.websiteFilter.WebsiteFilterScreen
import com.example.parental_control_app.viewmodels.websiteFilter.WebsiteFilterViewModel

class WebsiteFilterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityStarterHelper = ActivityStarterHelper(this)

        val kidProfileId = intent.getStringExtra("kidProfileId").toString()
        val websiteFilterViewModel = WebsiteFilterViewModel(kidProfileId, activityStarterHelper)

        setContent {
            WebsiteFilterScreen(viewModel = websiteFilterViewModel) {
                finish()
            }
        }
    }
}

