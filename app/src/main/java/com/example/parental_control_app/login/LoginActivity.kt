package com.example.parental_control_app.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.googleauth.GoogleOAuthActivity
import com.example.parental_control_app.registration.RegistrationActivity
import com.example.parental_control_app.startup.StartupActivity
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
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
        val loginViewModel = LoginViewModel()
        loginViewModel.setSignInCallback { startStartupActivity() }
        setContent {
            ParentalcontrolappTheme {
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
        startActivity(googleOAuthActivity)
    }

    private fun startRegistrationActivity() {
        val registrationActivity = Intent(this, RegistrationActivity::class.java)
        startActivity(registrationActivity)
        finish()
    }
}