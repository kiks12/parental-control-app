package com.example.parental_control_app.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BlockedAppsViewModel(
    private val profileId: String,
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

    private lateinit var back: () -> Unit

    init {
        viewModelScope.launch {
            val uid = usersRepository.getProfileUID(profileId)
            _appsState.value = appsRepository.getBlockedApps(uid)
            _iconsState.value = appsRepository.getAppIcons(uid, _appsState.value)
            async { _uidState.value = uid }.await()
            async { _loadingState.value = false }.await()
        }
    }

    fun addOnBackClick(callback: () -> Unit) {
        back = callback
    }

    fun onBackClick() : () -> Unit {
        return back
    }

    fun updateAppRestriction(appName: String, newRestriction: Boolean) {
        viewModelScope.launch {
            appsRepository.updateAppRestriction(_uidState.value!!, appName, newRestriction)
        }
    }
}