package com.example.parental_control_app.registration

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.parental_control_app.emailpasswordauth.EmailPasswordState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationViewModel : ViewModel() {

    private var auth : FirebaseAuth = Firebase.auth
    var credentialState = mutableStateOf(EmailPasswordState())
        private set

    private val _passwordVisibilityState = mutableStateOf(false)
    val passwordVisibilityState : Boolean
        get() = _passwordVisibilityState.value
    fun emailOnChange(newText: String) {
        this.credentialState.value = this.credentialState.value.copy(email = newText)
    }
    fun passwordOnChange(newText: String) {
        this.credentialState.value = this.credentialState.value.copy(password = newText)
    }

//    fun changePasswordVisibility() {
//        _passwordVisibilityState.value = !_passwordVisibilityState.value
//    }
    fun register() {
        auth.createUserWithEmailAndPassword(this.credentialState.value.email, this.credentialState.value.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Log.d(TAG, user.toString())
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                    updateUI(null)
                }
            }
    }
    fun registerWithGoogle() {

    }


}
