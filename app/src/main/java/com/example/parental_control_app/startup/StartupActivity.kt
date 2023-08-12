package com.example.parental_control_app.startup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.example.parental_control_app.R
import com.example.parental_control_app.children.ChildrenActivity
import com.example.parental_control_app.login.LoginActivity
import com.example.parental_control_app.parent.ParentActivity
import com.example.parental_control_app.sharedpreferences.SharedPreferencesHelper
import com.example.parental_control_app.toasthelper.ToastHelper
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.users.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class StartupActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private var profile : UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, Context.MODE_PRIVATE)

        profile = SharedPreferencesHelper.getProfile(sharedPreferences)

        val toastHelper = ToastHelper(this)
        val startupViewModel = StartupViewModel(toastHelper)
        startupViewModel.setSignOutCallback{ signOut() }
        startupViewModel.setRefreshCallback{ refresh() }
        startupViewModel.setSharedPreferences(sharedPreferences)
        startupViewModel.setStartParentActivity{ startParentActivity() }
        startupViewModel.setStartChildActivity{ startChildActivity() }

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
        val parentIntent = Intent(this, ParentActivity::class.java)
        startActivity(parentIntent)
        finish()
    }

    private fun startChildActivity() {
        val childIntent = Intent(this, ChildrenActivity::class.java)
        startActivity(childIntent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (profileIsParent()) startParentActivity()
        if (profileIsChild()) startChildActivity()
    }

    fun refresh() {
        finish()
        startActivity(intent)
    }

    fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}