package com.example.parental_control_app.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.parental_control_app.R
import com.example.parental_control_app.activities.children.ChildrenMainActivity
import com.example.parental_control_app.activities.parent.ParentMainActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.helpers.ProfileSignOutHelper
import com.example.parental_control_app.helpers.ResultLauncherHelper
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.screens.StartupScreen
import com.example.parental_control_app.viewmodels.StartupViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson

class StartupActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toastHelper: ToastHelper
    private lateinit var activityStarterHelper: ActivityStarterHelper
    private lateinit var profileSignOutHelper: ProfileSignOutHelper
    private lateinit var resultLauncherHelper: ResultLauncherHelper

    private lateinit var startupViewModel: StartupViewModel

    private var profile : UserProfile? = null

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        when(activityResult.resultCode) {
            RESULT_OK -> {
                val gson = Gson()
                val jsonData = activityResult.data?.getStringExtra("ActivityResult").toString()
                val newProfileData = gson.fromJson(jsonData, UserProfile::class.java)
                Log.w("NEW PROFILE", newProfileData.toString())
                startupViewModel.addProfile(newProfileData)
            }
            RESULT_CANCELED -> {}
        }
    }

    private fun isOverlayPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        if (!Settings.canDrawOverlays(this)) return false
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, Context.MODE_PRIVATE)
        profile = SharedPreferencesManager.getProfile(sharedPreferences)

        toastHelper = ToastHelper(this)
        activityStarterHelper = ActivityStarterHelper(this)
        resultLauncherHelper = ResultLauncherHelper(this, resultLauncher)
        profileSignOutHelper = ProfileSignOutHelper(this, sharedPreferences)

        when (isOverlayPermissionGranted()) {
            false -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        }
                    }
                    startActivity(intent)
                }
            }
            else -> {}
        }

        startupViewModel = StartupViewModel(toastHelper, activityStarterHelper, resultLauncherHelper)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(applicationContext.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)

        startupViewModel.setSharedPreferences(sharedPreferences)
        startupViewModel.googleSignOut = { googleSignInClient.signOut() }

        setContent {
            ParentalControlAppTheme {
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