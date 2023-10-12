package com.example.parental_control_app.activities.websiteFilter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.screens.websiteFilter.WebsiteFilterScreen
import com.example.parental_control_app.service.ParentalControlWebsiteFilter
import com.example.parental_control_app.viewmodels.websiteFilter.WebsiteFilterViewModel


class WebsiteFilterActivity : AppCompatActivity() {

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    private lateinit var websiteFilterViewModel : WebsiteFilterViewModel

    private fun isAccessibilityServiceEnabled(context: Context, accessibilityService: Class<*>?): Boolean {
        val expectedComponentName = ComponentName(context, accessibilityService!!)
        val enabledServicesSetting: String =
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
                ?: return false
        val colonSplitter = SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)

        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledService = ComponentName.unflattenFromString(componentNameString)
            if (enabledService != null && enabledService == expectedComponentName) return true
        }

        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val profile = SharedPreferencesManager.getProfile(sharedPreferences)

        val activityStarterHelper = ActivityStarterHelper(this)
        val kidProfileId = intent.getStringExtra("kidProfileId").toString()

        if (profile != null) {
            websiteFilterViewModel = WebsiteFilterViewModel(profile, kidProfileId, activityStarterHelper)
        }

        if (profile != null && profile.child && !isAccessibilityServiceEnabled(this, ParentalControlWebsiteFilter::class.java)) {
            resultLauncher.launch(Intent("android.settings.ACCESSIBILITY_SETTINGS"))
        }

        setContent {
            WebsiteFilterScreen(viewModel = websiteFilterViewModel) {
                finish()
            }
        }
    }
}

