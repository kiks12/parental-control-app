package com.example.parental_control_app.viewmodels

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.activities.LoginActivity
import com.example.parental_control_app.activities.children.ChildrenMainActivity
import com.example.parental_control_app.activities.parent.ParentMainActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UserState
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.screens.UserProfileType
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

data class StartupState(
    val user: UserState = UserState("", ""),
    val profiles: List<UserProfile> = emptyList(),
    val profileInput: UserProfile = UserProfile("","", "")
)

class StartupViewModel(
    private val toastHelper: ToastHelper,
    private val activityStarterHelper: ActivityStarterHelper,
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

    private lateinit var sharedPreferences: SharedPreferences

    init {
        updateUser()
        updateProfiles()
    }

    private fun updateUser() {
        viewModelScope.launch {
            val authUser = auth.currentUser
            val user = usersRepository.findUser(authUser?.uid.toString())
            _uiState.value = _uiState.value.copy(user = user)
        }
    }

    private fun updateProfiles() {
        viewModelScope.launch {
            val authUser = auth.currentUser
            val profiles = usersRepository.findUserProfiles(authUser?.uid.toString())
            _uiState.value = _uiState.value.copy(profiles = profiles)
        }
    }

    fun setSharedPreferences(preferences: SharedPreferences) {
        sharedPreferences = preferences
    }

    fun signOut() {
        auth.signOut()
        activityStarterHelper.startNewActivity(LoginActivity::class.java)
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
        viewModelScope.launch {
            updateUser()
            updateProfiles()
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

    fun onProfileClick(profile: UserProfile) {
        val editor = sharedPreferences.edit()
        editor.putString(SharedPreferencesHelper.PROFILE_KEY, SharedPreferencesHelper.createJsonString(profile))
        editor.apply()
        if (profile.child) startChildActivity()
        if (profile.parent) startParentActivity()
    }

    private fun startChildActivity() {
        activityStarterHelper.startNewActivity(ChildrenMainActivity::class.java)
    }

    private fun startParentActivity() {
        activityStarterHelper.startNewActivity(ParentMainActivity::class.java)
    }

}