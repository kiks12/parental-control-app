package com.example.parental_control_app.viewmodels.manageProfile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.parental_control_app.activities.manageProfile.ChildTestActivity
import com.example.parental_control_app.activities.manageProfile.TeenTestActivity
import com.example.parental_control_app.helpers.ResultLauncherHelper
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.repositories.users.UserProfile

data class CreateProfileState(
    val profile: UserProfile = UserProfile()
)

class CreateProfileVIewModel(
    private val resultLauncherHelper: ResultLauncherHelper,
    private val toastHelper: ToastHelper
) : ViewModel() {

    private val _state = mutableStateOf(CreateProfileState(
        profile = UserProfile(
            profileId = "",
            name = "",
            userId = "",
            age = "",
            birthday = 0L,
            parent = true,
            child = false,
            phoneNumber = "",
            phoneLock = false,
            password = ""
        )
    ))

    val state : CreateProfileState
        get() = _state.value


    fun onNameChange(newString: String) {
        _state.value = _state.value.copy(profile = _state.value.profile.copy(name = newString))
    }

    fun onPhoneNumberChange(newString: String) {
        _state.value = _state.value.copy(profile = _state.value.profile.copy(phoneNumber = newString))
    }

    fun onAgeChange(newString: String) {
        _state.value = _state.value.copy(profile = _state.value.profile.copy(age = newString))
    }

    fun onPasswordChange(newString: String) {
        _state.value = _state.value.copy(profile = _state.value.profile.copy(password = newString))
    }

    fun onBirthdayChange(newBirthday: Long) {
        _state.value = _state.value.copy(profile = _state.value.profile.copy(birthday = newBirthday))
    }

    fun changeProfileType(state: Boolean) {
        if (state) {
            _state.value = _state.value.copy(
                profile = _state.value.profile.copy(
                    parent = true,
                    child = false,
                )
            )
        } else {
            _state.value = _state.value.copy(
                profile = _state.value.profile.copy(
                    parent = false,
                    child = true,
                )
            )
        }
    }

    fun onMaturityLevelChange(newString: String) {
        _state.value = _state.value.copy(profile = _state.value.profile.copy(maturityLevel = newString))
    }

    fun startSurvey() {
        if (_state.value.profile.age.isEmpty()) {
            toastHelper.makeToast("Enter child age first to answer survey")
            return
        }
        val age = _state.value.profile.age.toInt()
        if (age in 0..11) resultLauncherHelper.launch(ChildTestActivity::class.java)
        if (age in 12 .. 17) resultLauncherHelper.launch(TeenTestActivity::class.java)
    }
}