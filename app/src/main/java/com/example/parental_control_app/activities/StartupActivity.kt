package com.example.parental_control_app.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import com.example.parental_control_app.activities.children.ChildrenMainActivity
import com.example.parental_control_app.activities.parent.ParentMainActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.helpers.ProfileSignOutHelper
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.screens.StartupScreen
import com.example.parental_control_app.viewmodels.StartupViewModel

class StartupActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toastHelper: ToastHelper
    private lateinit var activityStarterHelper: ActivityStarterHelper
    private lateinit var profileSignOutHelper: ProfileSignOutHelper
    private var profile : UserProfile? = null

    private fun isOverlayPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        if (!Settings.canDrawOverlays(this)) return false
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, Context.MODE_PRIVATE)
        profile = SharedPreferencesHelper.getProfile(sharedPreferences)

        toastHelper = ToastHelper(this)
        activityStarterHelper = ActivityStarterHelper(this)
        profileSignOutHelper = ProfileSignOutHelper(this, sharedPreferences)

        when (isOverlayPermissionGranted()) {
            true -> initializeUI()
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        }
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun initializeUI() {
        val startupViewModel = StartupViewModel(toastHelper, activityStarterHelper, profileSignOutHelper)
        startupViewModel.setSharedPreferences(sharedPreferences)

        setContent {
            ParentalcontrolappTheme {
                StartupScreen(startupViewModel)
            }
        }
    }

    private fun profileIsParent() : Boolean {
        return profile != null && profile!!.parent
    }

    private fun profileIsChild() : Boolean {
        return profile != null && profile!!.child
    }

    private fun startParentActivity() {
        val parentIntent = Intent(this, ParentMainActivity::class.java)
        startActivity(parentIntent)
    }

    private fun startChildActivity() {
        val childIntent = Intent(this, ChildrenMainActivity::class.java)
        startActivity(childIntent)
    }

    override fun onStart() {
        super.onStart()
        if (profileIsParent()) startParentActivity()
        if (profileIsChild()) startChildActivity()
    }
}