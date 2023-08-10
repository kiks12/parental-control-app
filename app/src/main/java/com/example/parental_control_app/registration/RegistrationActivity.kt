package com.example.parental_control_app.registration

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.googleauth.GoogleOAuthActivity
import com.example.parental_control_app.login.LoginActivity
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val registrationViewModel = RegistrationViewModel()
        setContent {
            ParentalcontrolappTheme {
                RegistrationScreen(
                    registrationViewModel,
                    { startLoginActivity() },
                    { signUpWithGoogle() }
                )
            }
        }
    }

    private fun signUpWithGoogle() {
        val intent = Intent(this, GoogleOAuthActivity::class.java)
        startActivity(intent)
    }

    private fun startLoginActivity() {
        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
        finish()
    }
}