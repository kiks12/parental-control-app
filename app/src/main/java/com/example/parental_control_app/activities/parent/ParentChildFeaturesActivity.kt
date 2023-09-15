package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.screens.parent.ParentChildFeaturesScreen
import com.example.parental_control_app.viewmodels.parent.ParentChildFeaturesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ParentChildFeaturesActivity : AppCompatActivity() {

    private val usersRepository = UsersRepository()
    private lateinit var kidProfile : UserProfile

    private var parentChildFeaturesViewModel : ParentChildFeaturesViewModel? = null
    private val loading = mutableStateOf(true)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityStarterHelper = ActivityStarterHelper(this)
        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val profile = SharedPreferencesManager.getProfile(sharedPreferences)
        val kidProfileId = intent.getStringExtra("profileId")

        lifecycleScope.launch {
            val uid = usersRepository.getProfileUID(kidProfileId.toString())
            kidProfile = usersRepository.getProfile(uid)

            withContext(Dispatchers.Main) {

                parentChildFeaturesViewModel = ParentChildFeaturesViewModel()

                parentChildFeaturesViewModel?.setProfileId(profile?.profileId!!)
                parentChildFeaturesViewModel?.setKidProfileId(kidProfileId.toString())
                parentChildFeaturesViewModel?.setKidProfile(kidProfile)
                parentChildFeaturesViewModel?.addOnBackClick { finish() }
                parentChildFeaturesViewModel?.setActivityStartHelper(activityStarterHelper)

                loading.value = false
            }
        }

        setContent {
            if (loading.value && parentChildFeaturesViewModel == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ParentChildFeaturesScreen(parentChildFeaturesViewModel!!)
            }
        }
    }
}