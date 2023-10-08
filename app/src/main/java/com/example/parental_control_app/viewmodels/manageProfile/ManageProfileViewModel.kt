package com.example.parental_control_app.viewmodels.manageProfile

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.activities.manageProfile.CreateProfileActivity
import com.example.parental_control_app.data.ResponseStatus
import com.example.parental_control_app.helpers.ResultLauncherHelper
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UserState
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class ManageProfileViewModel(
    private val resultLauncherHelper: ResultLauncherHelper,
): ViewModel() {

    private val usersRepository = UsersRepository()
    private val auth = Firebase.auth

    private val _loadingState = mutableStateOf(true)
    private val _profilesState = mutableStateOf<List<UserProfile>>(listOf())
    private val _userState = mutableStateOf(UserState("", ""))
    val snackBarHostState = SnackbarHostState()

    val profiles : List<UserProfile>
        get() = _profilesState.value

    val loading : Boolean
        get() = _loadingState.value

    init {
        viewModelScope.launch {
            async { _loadingState.value = true }.await()
            updateUser()
            updateProfiles()
            async { _loadingState.value = false }.await()
        }
    }

    private fun updateUser() {
        viewModelScope.launch {
            val authUser = auth.currentUser
            val user = usersRepository.findUser(authUser?.uid.toString())
            _userState.value = user
        }
    }

    private fun updateProfiles() {
        viewModelScope.launch {
            val authUser = auth.currentUser
            val profiles = usersRepository.findUserProfiles(authUser?.uid.toString())
            _profilesState.value = profiles
        }
    }

    fun deleteProfile(selectedProfile: UserProfile) {
        viewModelScope.launch {
            val response = usersRepository.deleteProfile(selectedProfile)

            if (response.status == ResponseStatus.SUCCESS) {
                _profilesState.value = _profilesState.value.filter { profile -> profile.profileId != selectedProfile.profileId }
            }

            snackBarHostState.showSnackbar(response.message)
        }
    }

    fun addProfile(newProfile: UserProfile) {
        viewModelScope.launch {
            val profileId = _userState.value.userId + newProfile.name
            val plainPass = newProfile.password
            val hashedPassword = BCrypt.hashpw(plainPass, BCrypt.gensalt())
            val hashedProfileId = BCrypt.hashpw(profileId, BCrypt.gensalt())

            newProfile.profileId = hashedProfileId
            newProfile.password = hashedPassword
            newProfile.userId = _userState.value.userId

            val response = usersRepository.saveProfile(newProfile)

            if (response.status == ResponseStatus.SUCCESS) {
                _profilesState.value = _profilesState.value.plus(newProfile)
            }

            snackBarHostState.showSnackbar(response.message)
        }
    }

    fun startCreateProfileActivity() {
        resultLauncherHelper.launch(CreateProfileActivity::class.java)
    }

}