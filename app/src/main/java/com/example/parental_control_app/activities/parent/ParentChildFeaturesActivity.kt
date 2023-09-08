package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.screens.parent.ParentChildFeaturesScreen
import com.example.parental_control_app.viewmodels.parent.ParentChildFeaturesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ParentChildFeaturesActivity : AppCompatActivity() {

    private val usersRepository = UsersRepository()
    private lateinit var kidProfile : UserProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, MODE_PRIVATE)
        val profile = SharedPreferencesHelper.getProfile(sharedPreferences)
        val kidProfileId = intent.getStringExtra("profileId")

        lifecycleScope.launch(Dispatchers.IO) {
            var uid = "null"
            async { uid = usersRepository.getProfileUID(kidProfileId.toString()) }.await()
            async { kidProfile = usersRepository.getProfile(uid) }.await()
            async {
                withContext(Dispatchers.Main) {
                    val activityStarterHelper = ActivityStarterHelper(this@ParentChildFeaturesActivity)

                    val parentChildFeaturesViewModel = ParentChildFeaturesViewModel()

                    parentChildFeaturesViewModel.setProfileId(profile?.profileId!!)
                    parentChildFeaturesViewModel.setKidProfileId(kidProfileId.toString())
                    parentChildFeaturesViewModel.setKidProfile(kidProfile)
                    parentChildFeaturesViewModel.addOnBackClick { finish() }
                    parentChildFeaturesViewModel.setActivityStartHelper(activityStarterHelper)

                    setContent {
                        ParentChildFeaturesScreen(parentChildFeaturesViewModel)
                    }
                }
            }.await()
        }
    }
}