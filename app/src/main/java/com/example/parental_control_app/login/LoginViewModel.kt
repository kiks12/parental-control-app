package com.example.parental_control_app.login

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.parental_control_app.emailpasswordauth.EmailPasswordState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    private lateinit var signInCallback: () -> Unit
    private var auth : FirebaseAuth = Firebase.auth
    var credentialState = mutableStateOf(EmailPasswordState())
        private set
    private val _passwordVisibilityState = mutableStateOf(false)
    val passwordVisibilityState : Boolean
        get() = _passwordVisibilityState.value

    fun emailOnChange(newText: String) {
        credentialState.value = credentialState.value.copy(email = newText)
    }
    fun passwordOnChange(newText: String) {
        credentialState.value = credentialState.value.copy(password = newText)
    }

    fun setSignInCallback(callback: () -> Unit) {
        signInCallback = callback
    }

    fun login() {
        auth.signInWithEmailAndPassword(credentialState.value.email, credentialState.value.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.w(TAG, it.result.user?.uid.toString())
                    signInCallback()
                } else {
                    Log.w(TAG, it.toString())
                }
            }
    }

//    fun loginWithGoogle() {}
}