package com.example.parental_control_app.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.ResponseStatus
import com.example.parental_control_app.repositories.emailpasswordauth.EmailPasswordState
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch

class LoginViewModel(
    private val toastHelper: ToastHelper
) : ViewModel() {

    private val usersRepository = UsersRepository()

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

    fun onCheckBoxChange(newState : Boolean) {
        _passwordVisibilityState.value = newState
    }

    private suspend fun emailIsUsed(email: String) : Boolean {
        val result = CompletableDeferred<Boolean>()

        viewModelScope.launch {
            val response = usersRepository.findUserByEmail(email)
            if (response.status == ResponseStatus.SUCCESS) {
                if (response.data != null) {
                    result.complete(response.data["used"] as Boolean)
                }
            }
        }

        return result.await()
    }

    fun login() {
        if (
            credentialState.value.email.isEmpty() || credentialState.value.email.isBlank() ||
            credentialState.value.password.isEmpty() || credentialState.value.password.isBlank()
            ) {
            toastHelper.makeToast("Please fill up all the field")
            return
        }

        auth.signInWithEmailAndPassword(credentialState.value.email, credentialState.value.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = auth.currentUser ?: return@addOnCompleteListener

                    viewModelScope.launch {
                        if (!emailIsUsed(user.email.toString())) {
                            toastHelper.makeToast("Email not registered. Please register an account")
                            return@launch
                        } else {
                            toastHelper.makeToast("Signed in as ${user.email}")
                            signInCallback()
                        }
                    }
                } else {
                    toastHelper.makeToast(it.exception?.localizedMessage.toString())
                }
            }
    }

}