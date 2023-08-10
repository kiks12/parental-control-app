package com.example.parental_control_app.startup

import androidx.lifecycle.ViewModel

class StartupViewModel : ViewModel() {

    private lateinit var signOutCallback: () -> Unit

    fun setSignOutCallback(callback: () -> Unit) {
        signOutCallback = callback
    }

    fun signOut() {
        signOutCallback()
    }

}