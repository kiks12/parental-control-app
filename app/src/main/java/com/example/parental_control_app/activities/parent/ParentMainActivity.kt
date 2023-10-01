package com.example.parental_control_app.activities.parent

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.parental_control_app.helpers.ProfileSignOutHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.screens.parent.ParentNavigationScreen
import com.example.parental_control_app.service.AppBlockerService
import com.example.parental_control_app.service.PhoneLockerService
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.parent.ParentHomeViewModel
import com.example.parental_control_app.viewmodels.parent.ParentNavigationViewModel

class ParentMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val profileSignOutHelper = ProfileSignOutHelper(this, sharedPreferences)

        val parentHomeViewModel = ParentHomeViewModel()
        parentHomeViewModel.addOnChildrenCardClick(this::onChildrenCardClick)

        val parentNavigationViewModel = ParentNavigationViewModel()
        parentNavigationViewModel.setParentHomeViewModel(parentHomeViewModel)
        parentNavigationViewModel.addOnSignOut { profileSignOutHelper.signOut() }

        setContent {
            ParentalControlAppTheme {
                Surface (
                    modifier = Modifier.fillMaxSize()
                ){
                    ParentNavigationScreen(parentNavigationViewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        stopService(Intent(applicationContext, AppBlockerService::class.java))
        stopService(Intent(applicationContext, PhoneLockerService::class.java))
    }

    private fun onChildrenCardClick(profileId: String) {
        val intent = Intent(this, ParentChildFeaturesActivity::class.java)
        intent.putExtra("profileId", profileId)
        startActivity(intent)
    }
}