package com.example.parental_control_app.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.screens.LoginScreen
import com.example.parental_control_app.viewmodels.LoginViewModel
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) startStartupActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        val toastHelper = ToastHelper(this)
        val loginViewModel = LoginViewModel(toastHelper)
        loginViewModel.setSignInCallback { startStartupActivity() }

        setContent {
            ParentalControlAppTheme {
                LoginScreen(
                    loginViewModel,
                    { signInWithGoogle() },
                    { startRegistrationActivity() }
                )
            }
        }
    }

    private fun startStartupActivity() {
        val startupActivity = Intent(this, StartupActivity::class.java)
        startActivity(startupActivity)
        finish()
    }

    private fun signInWithGoogle() {
        val googleOAuthActivity = Intent(this, GoogleOAuthActivity::class.java)
        val bundle = Bundle()
        bundle.putString("TYPE", GoogleOAuthActivityType.SIGNIN.name)
        googleOAuthActivity.putExtra("Extras", bundle)
        startActivity(googleOAuthActivity)
    }

    private fun startRegistrationActivity() {
        val registrationActivity = Intent(this, RegistrationActivity::class.java)
        startActivity(registrationActivity)
        finish()
    }
}