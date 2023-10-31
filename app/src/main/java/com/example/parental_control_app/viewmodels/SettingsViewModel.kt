package com.example.parental_control_app.viewmodels

import androidx.lifecycle.ViewModel
import com.example.parental_control_app.activities.ChangePasscodeActivity
import com.example.parental_control_app.activities.manageProfile.ManageProfileActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper

enum class SettingsType {
    PARENT,
    CHILD
}

class SettingsViewModel(
    type: SettingsType,
    private val activityStarterHelper: ActivityStarterHelper? = null,
): ViewModel(){

    var signOut : () -> Unit = {}
    val typeProvider = type

    fun startManageProfileActivity() {
        activityStarterHelper?.startNewActivity(ManageProfileActivity::class.java)
    }

    fun startChangePasscodeActivity() {
        activityStarterHelper?.startNewActivity(ChangePasscodeActivity::class.java)
    }

}