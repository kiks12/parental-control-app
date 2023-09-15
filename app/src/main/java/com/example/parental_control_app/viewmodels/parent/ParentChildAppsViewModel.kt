package com.example.parental_control_app.viewmodels.parent

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParentChildAppsViewModel(
    private val profileId: String,
    private val appsRepository: AppsRepository = AppsRepository(),
    private val usersRepository: UsersRepository = UsersRepository(),
) : ViewModel(){

    private val _profile = mutableStateOf(UserProfile())
    private val _loadingState = mutableStateOf(true)
    private val _suggestionsState = mutableStateListOf<UserApps>()
    private val _appsState = mutableStateListOf<UserApps>()
    private val _iconsState = mutableStateOf(mapOf<String, String>())
    private lateinit var back : () -> Unit

    val suggestionsState : List<UserApps>
        get() = _suggestionsState

    val appsState : List<UserApps>
        get() = _appsState

    val iconState : Map<String, String>
        get() = _iconsState.value

    val loadingState : Boolean
        get() = _loadingState.value


    init {
        viewModelScope.launch {
            val uid = usersRepository.getProfileUID(profileId)
            _profile.value = usersRepository.getProfile(uid)

            val suggestions = appsRepository.getSuggestedAppRestriction(_profile.value.age.toInt())
            val apps = appsRepository.getApps(uid)
            _iconsState.value = appsRepository.getAppIcons(uid, apps)

            withContext(Dispatchers.Default) {
                apps.forEach { app ->
                    if (suggestions.contains(app.packageName)) {
                        _suggestionsState.add(app)
                    } else {
                        _appsState.add(app)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                _loadingState.value = false
            }
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
            val uid = usersRepository.getProfileUID(profileId)
            appsRepository.updateAppRestriction(uid, appName, newRestriction)
        }
    }
}