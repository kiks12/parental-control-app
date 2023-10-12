package com.example.parental_control_app.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.ResponseStatus
import com.example.parental_control_app.repositories.emailpasswordauth.EmailPasswordState
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.repositories.users.UserState
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val toastHelper: ToastHelper,
    private val usersRepository: UsersRepository = UsersRepository()
) : ViewModel() {

    private lateinit var signUpCallback : () -> Unit
    private var auth = Firebase.auth

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

    fun changePasswordVisibility(newVal: Boolean) {
        _passwordVisibilityState.value = newVal
    }

    fun setSignUpCallback(callback: () -> Unit) {
        signUpCallback = callback
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

    fun register() {
        if (credentialState.value.email.isEmpty() || credentialState.value.email.isBlank() ||
            credentialState.value.password.isEmpty() || credentialState.value.password.isBlank()) {
            toastHelper.makeToast("Please fill up all the field")
            return
        }

        auth.createUserWithEmailAndPassword(credentialState.value.email, credentialState.value.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser ?: return@addOnCompleteListener

                    viewModelScope.launch {
                        if (emailIsUsed(user.email.toString())) {
                            toastHelper.makeToast("Email is already used.")
                            return@launch
                        } else {
                            val newUser = UserState(
                                userId = auth.uid.toString(),
                                email = user.email.toString(),
                            )

                            val msg = usersRepository.createUser(newUser)
                            toastHelper.makeToast(msg)

                            toastHelper.makeToast("Signed in as ${user.email}")
                            signUpCallback()
                        }
                    }
                } else {
                    toastHelper.makeToast(task.exception?.localizedMessage.toString())
                }
            }
    }

}
