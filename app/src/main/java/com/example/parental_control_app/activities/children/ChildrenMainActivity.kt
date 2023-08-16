package com.example.parental_control_app.activities.children

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.parental_control_app.screens.children.ChildrenScreen
import com.example.parental_control_app.viewmodels.children.ChildrenViewModel
import com.example.parental_control_app.helpers.SignOutHelper
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.service.AppLockerService
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme

class ChildrenMainActivity : AppCompatActivity() {

    private lateinit var signOutHelper: SignOutHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, Context.MODE_PRIVATE)
        signOutHelper = SignOutHelper(this, sharedPreferences)

        val profile = SharedPreferencesHelper.getProfile(sharedPreferences)

        val activityStarterHelper = ActivityStarterHelper(this)
        val childrenViewModel = ChildrenViewModel(activityStarterHelper)
        childrenViewModel.setSignOutFunction { signOutHelper.signOut() }
        childrenViewModel.setProfile(profile!!)

//        startForegroundService(Intent(this, AppLockerService::class.java))

        setContent {
            ParentalcontrolappTheme {
                Surface (
                    modifier = Modifier.fillMaxSize()
                ) {
                    ChildrenScreen(childrenViewModel)
                }
            }
        }
    }
}