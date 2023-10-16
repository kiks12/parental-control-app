package com.example.parental_control_app.activities.manageProfile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.parental_control_app.helpers.ResultLauncherHelper
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.manageProfile.ManageProfileViewModel
import com.google.gson.Gson

class ManageProfileActivity : AppCompatActivity() {

    private lateinit var manageProfileViewModel: ManageProfileViewModel
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        when (activityResult.resultCode) {
            RESULT_OK -> {
                val gson = Gson()
                val jsonData = activityResult.data?.getStringExtra("ActivityResult")
                val newProfile = gson.fromJson(jsonData, UserProfile::class.java)
                manageProfileViewModel.addProfile(newProfile)
            }
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val resultLauncherHelper = ResultLauncherHelper(this, resultLauncher)
        manageProfileViewModel = ManageProfileViewModel(resultLauncherHelper)

        setContent {
            val loading = manageProfileViewModel.loading
            val profiles = manageProfileViewModel.profiles

            ParentalControlAppTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = manageProfileViewModel.snackBarHostState) },
                    topBar = {
                        TopAppBar(
                            title = { Text("Manage Profiles")},
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Rounded.ArrowBack, "Go Back")
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = manageProfileViewModel::startCreateProfileActivity,
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(Icons.Rounded.Add, "Add Profile")
                        }
                    }
                ){ innerPadding ->
                    if (!loading) {
                        LazyColumn(Modifier.padding(innerPadding)) {
                            items(profiles) { profile ->
                                ListItem(
                                    headlineContent = { Text(profile.name) },
                                    supportingContent = { if (profile.parent) Text("Parent") else Text("Child")},
                                    trailingContent = {
                                        if (profile.child) {
                                            IconButton(onClick = { manageProfileViewModel.deleteProfile(profile) }) {
                                                Icon(Icons.Outlined.Delete, "Delete")
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    } else {
                        Box(
                            Modifier.padding(innerPadding).fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}