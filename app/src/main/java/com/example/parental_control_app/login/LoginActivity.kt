package com.example.parental_control_app.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.registration.RegistrationActivity
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        val loginViewModel = LoginViewModel()

        setContent {
            ParentalcontrolappTheme {
                LoginScreen(loginViewModel, { startRegistrationActivity() } )
            }
        }
    }
    fun startRegistrationActivity() {
        val registrationActivity = Intent(this, RegistrationActivity::class.java)
        startActivity(registrationActivity)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        Log.w(TAG, currentUser?.displayName.toString())
    }
}