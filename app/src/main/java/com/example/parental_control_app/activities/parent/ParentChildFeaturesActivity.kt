package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.screens.parent.ParentChildFeaturesScreen
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
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
            ParentalControlAppTheme {
                if (loading.value && parentChildFeaturesViewModel == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Box {
                        ParentChildFeaturesScreen(parentChildFeaturesViewModel!!)
                        if (kidProfile.uninstalled) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(50, 50, 50, 190)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column (
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    Text(
                                        "Uninstalled!",
                                        fontSize = 50.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        "The app may be uninstalled\nin your child's device. Please reinstall it\nagain to enable parental control features.",
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(30.dp))
                                    Text(
                                        "NOTE: This may be inaccurate, it is\npossible that your child's\ndevice is disconnected from the internet.",
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(50.dp))
                                    FilledTonalButton(onClick = { finish() }) {
                                        Text(text = "Back")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}