package com.example.parental_control_app.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.repositories.AppsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BlockedAppsViewModel(
    private val profileId: String,
    private val appsRepository: AppsRepository = AppsRepository(),
) : ViewModel(){

    private val _loadingState = mutableStateOf(true)
    private val _appsState = mutableStateOf(listOf<UserApps>())
    private val _iconsState = mutableStateOf(mapOf<String, String>())
    val appsState : List<UserApps>
        get() = _appsState.value

    val iconsState : Map<String, String>
        get() = _iconsState.value

    val loadingState : Boolean
        get() = _loadingState.value

    private lateinit var back: () -> Unit

    init {
        viewModelScope.launch {
            async { _appsState.value = appsRepository.getBlockedApps(profileId) }.await()
            async { _iconsState.value = appsRepository.getAppIcons(profileId) }.await()
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
            async {
                appsRepository.updateAppRestriction(profileId, appName, newRestriction)
            }.await()
        }
    }
}