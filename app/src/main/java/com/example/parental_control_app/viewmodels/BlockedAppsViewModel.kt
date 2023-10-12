package com.example.parental_control_app.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BlockedAppsViewModel(
    profile: UserProfile,
    private val kidProfileId: String,
    private val appsRepository: AppsRepository = AppsRepository(),
    private val usersRepository: UsersRepository = UsersRepository(),
) : ViewModel(){

    private val _loadingState = mutableStateOf(true)
    private val _appsState = mutableStateOf(listOf<UserApps>())
    private val _iconsState = mutableStateOf(mapOf<String, String>())
    private val _uidState = mutableStateOf<String?>(null)

    val appsState : List<UserApps>
        get() = _appsState.value

    val iconsState : Map<String, String>
        get() = _iconsState.value

    val loadingState : Boolean
        get() = _loadingState.value

    val profileState = profile

    init {
        viewModelScope.launch {
            val uid = usersRepository.getProfileUID(kidProfileId)
            _loadingState.value = true
            async {
                _appsState.value = appsRepository.getBlockedApps(uid)
                _iconsState.value = appsRepository.getAppIcons(uid, _appsState.value)
                _uidState.value = uid
            }.await()
            async { _loadingState.value = false }.await()
        }
    }

    fun updateAppRestriction(appName: String, newRestriction: Boolean) {
        viewModelScope.launch {
            appsRepository.updateAppRestriction(_uidState.value!!, appName, newRestriction)
            usersRepository.updateBlockStatus(_uidState.value!!, true)
        }
    }

    fun updateAppScreenTimeLimit(appName: String, newTimeLimit: Long) {
        viewModelScope.launch {
            appsRepository.updateAppScreenTimeLimit(_uidState.value!!, appName, newTimeLimit)
        }
    }
}