package com.example.parental_control_app.viewmodels

import androidx.lifecycle.ViewModel

enum class SettingsType {
    PARENT,
    CHILD
}

class SettingsViewModel(type: SettingsType): ViewModel(){

    var signOut : () -> Unit = {}
    val typeProvider = type

}