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
    profile: UserProfile,
    private val kidProfileId: String,
    private val contentRating: Int,
    private val appsRepository: AppsRepository = AppsRepository(),
    private val usersRepository: UsersRepository = UsersRepository(),
) : ViewModel(){

    private val _kidProfile = mutableStateOf(UserProfile())
    private val _loadingState = mutableStateOf(true)
    private val _recommendationState = mutableStateListOf<UserApps>()
    private val _backupRecommendationState = mutableStateListOf<UserApps>()
    private val _appsState = mutableStateListOf<UserApps>()
    private val _backupAppsState = mutableStateListOf<UserApps>()
    private val _iconsState = mutableStateOf(mapOf<String, String>())

    val recommendationState : List<UserApps>
        get() = _recommendationState

    val appsState : List<UserApps>
        get() = _appsState

    val iconState : Map<String, String>
        get() = _iconsState.value

    val loadingState : Boolean
        get() = _loadingState.value

    val profileState = profile

    init {
        viewModelScope.launch {
            val uid = usersRepository.getProfileUID(kidProfileId)
            _kidProfile.value = usersRepository.getProfile(uid)

            val apps = appsRepository.getApps(uid)
            val recommendations = appsRepository.getAppBlockingRecommendation(apps, contentRating)
            _iconsState.value = appsRepository.getAppIcons(uid, apps)

            withContext(Dispatchers.Default) {
                apps.forEach { app ->
                    if (recommendations.contains(app.packageName)) {
                        val appCopy = app.copy(restricted = true)
                        _recommendationState.add(appCopy)
                        _backupRecommendationState.add(appCopy)
                        withContext(Dispatchers.IO) {
                            updateAppRestriction(app.packageName, true)
                        }
                    } else {
                        _appsState.add(app)
                        _backupAppsState.add(app)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                _loadingState.value = false
            }
        }
    }

    fun updateAppRestriction(appName: String, newRestriction: Boolean) {
        viewModelScope.launch {
            val uid = usersRepository.getProfileUID(kidProfileId)
            appsRepository.updateAppRestriction(uid, appName, newRestriction)
            usersRepository.updateBlockStatus(uid, true)
        }
    }

    fun updateAppScreenTimeLimit(appName: String, newTimeLimit: Long){
        viewModelScope.launch {
            val uid = usersRepository.getProfileUID(kidProfileId)
            appsRepository.updateAppScreenTimeLimit(uid, appName, newTimeLimit)
        }
    }

    fun refresh() {
        _recommendationState.clear()
        _recommendationState.addAll(_backupRecommendationState)
        _appsState.clear()
        _appsState.addAll(_backupAppsState)
    }

    fun search(query: String) {
        val filteredRecommendation = _backupRecommendationState.filter { userApps -> userApps.label.lowercase().contains(query.lowercase()) }
        val filteredApps = _backupAppsState.filter { userApps -> userApps.label.lowercase().contains(query.lowercase()) }
        _recommendationState.clear()
        _recommendationState.addAll(filteredRecommendation)
        _appsState.clear()
        _appsState.addAll(filteredApps)
    }
}