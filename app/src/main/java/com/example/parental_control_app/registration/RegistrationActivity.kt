package com.example.parental_control_app.registration

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.googleauth.GoogleOAuthActivity
import com.example.parental_control_app.googleauth.GoogleOAuthActivityType
import com.example.parental_control_app.login.LoginActivity
import com.example.parental_control_app.startup.StartupActivity
import com.example.parental_control_app.toasthelper.ToastHelper
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toastHelper = ToastHelper(this)
        val registrationViewModel = RegistrationViewModel(toastHelper)
        registrationViewModel.setSignUpCallback { startStartupActivity() }

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

    private fun startStartupActivity() {
        val startupActivity = Intent(this, StartupActivity::class.java)
        startActivity(startupActivity)
        finish()
    }

    private fun signUpWithGoogle() {
        val googleOAuthActivity = Intent(this, GoogleOAuthActivity::class.java)
        val bundle = Bundle()
        bundle.putString("TYPE", GoogleOAuthActivityType.SIGNUP.name)
        googleOAuthActivity.putExtra("Extras", bundle)
        startActivity(googleOAuthActivity)
    }

    private fun startLoginActivity() {
        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
        finish()
    }
}