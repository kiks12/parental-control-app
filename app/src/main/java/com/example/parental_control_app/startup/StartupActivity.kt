package com.example.parental_control_app.startup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.example.parental_control_app.R
import com.example.parental_control_app.login.LoginActivity
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class StartupActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContent {
            ParentalcontrolappTheme {
//                StartupScreen(StartupViewModel())
                Button(onClick = { signOut() } ) {
                    Text("Sign Out")
                }
            }
        }
    }

    fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}