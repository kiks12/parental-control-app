package com.example.parental_control_app.registration

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.emailpasswordauth.EmailPasswordState
import com.example.parental_control_app.toasthelper.ToastHelper
import com.example.parental_control_app.users.UserState
import com.example.parental_control_app.users.UsersRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    fun register() {
        auth.createUserWithEmailAndPassword(credentialState.value.email, credentialState.value.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val newUser = UserState(
                        userId = auth.uid.toString(),
                        email = user?.email.toString(),
                        parents = emptyList(),
                        children = emptyList()
                    )

                    viewModelScope.launch {
                        val msg = usersRepository.createUser(newUser)
                        toastHelper.makeToast(msg)
                    }

                    toastHelper.makeToast("Signed in as ${user?.displayName}")
                    signUpCallback()
                } else {
                    toastHelper.makeToast(task.exception?.localizedMessage.toString())
                }
            }
    }

}
