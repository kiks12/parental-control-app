package com.example.parental_control_app.startup

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.toasthelper.ToastHelper
import com.example.parental_control_app.users.UserProfile
import com.example.parental_control_app.users.UsersRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class StartupViewModel(
    private val toastHelper: ToastHelper,
    private val usersRepository: UsersRepository = UsersRepository(),
) : ViewModel() {

    private val auth = Firebase.auth
    private val _uiState = mutableStateOf(
        StartupState(
            profiles = emptyList(),
            profileInput = UserProfile(
                profileId = "",
                name = "",
                phoneNumber = "",
                password = "",
                userId = "",
                parent = true,
            )
        )
    )
    val uiState : StartupState
        get() = _uiState.value

    private lateinit var signOutCallback: () -> Unit
    private lateinit var refresh: () -> Unit

    init {
        updateUser()
        updateProfiles()
    }

    fun updateUser() {
        viewModelScope.launch {
            val authUser = auth.currentUser
            val user = usersRepository.findUser(authUser?.uid.toString())
            _uiState.value = _uiState.value.copy(user = user)
        }
    }

    fun updateProfiles() {
        viewModelScope.launch {
            val authUser = auth.currentUser
            val profiles = usersRepository.findUserProfiles(authUser?.uid.toString())
            Log.w(TAG, profiles.toString())
            _uiState.value = _uiState.value.copy(profiles = profiles)
        }
    }

    fun setSignOutCallback(callback: () -> Unit) {
        signOutCallback = callback
    }

    fun setRefreshCallback(callback: () -> Unit) {
        refresh = callback
    }

    fun signOut() {
        signOutCallback()
    }

    fun saveProfiles() {
        viewModelScope.launch {
            val msg = usersRepository.saveProfiles(_uiState.value.profiles)
            toastHelper.makeToast(msg)
        }
        viewModelScope.launch {
            val msgTwo = usersRepository.updateFirstSignIn(_uiState.value.user.userId)
            toastHelper.makeToast(msgTwo)
        }
    }
    fun createProfile(closeBottomSheet: () -> Unit) {
        val profileId = _uiState.value.user.userId + _uiState.value.profileInput.name
        val plainPass = _uiState.value.profileInput.password
        val hashedPassword = BCrypt.hashpw(plainPass, BCrypt.gensalt())
        val hashedProfileId = BCrypt.hashpw(profileId, BCrypt.gensalt())
        _uiState.value = _uiState.value.copy(
            profileInput = _uiState.value.profileInput.copy(
                profileId = hashedProfileId,
                password = hashedPassword,
                userId = _uiState.value.user.userId
            )
        )
        _uiState.value = _uiState.value.copy(
            profiles = _uiState.value.profiles.plus(_uiState.value.profileInput),
            profileInput = UserProfile(
                profileId = "",
                name = "",
                userId = "",
                phoneNumber = "",
                password = "",
                parent = true,
            )
        )
        closeBottomSheet()
    }

    fun onNameChange(newString: String) {
        _uiState.value = _uiState.value.copy(profileInput = _uiState.value.profileInput.copy(name = newString))
    }

    fun onPhoneNumberChange(newString: String) {
        _uiState.value = _uiState.value.copy(profileInput = _uiState.value.profileInput.copy(phoneNumber = newString))
    }

    fun onPasswordChange(newString: String) {
        _uiState.value = _uiState.value.copy(profileInput = _uiState.value.profileInput.copy(password = newString))
    }

    fun onTypeChange(newType: String) {
        if (newType == UserProfileType.CHILD.name) {
            _uiState.value = _uiState.value.copy(profileInput = _uiState.value.profileInput.copy(password = null))
        }
        _uiState.value = _uiState.value.copy(profileInput = _uiState.value.profileInput.copy(
            parent = newType == UserProfileType.PARENT.name,
            child = newType == UserProfileType.CHILD.name,
        ))
    }

}