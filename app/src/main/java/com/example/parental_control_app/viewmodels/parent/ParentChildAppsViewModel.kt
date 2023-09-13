package com.example.parental_control_app.viewmodels.parent

import android.util.Log
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.helpers.ScreenTimeHelper
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParentChildAppsViewModel(
    private val profileId: String,
    private val screenTimeHelper : ScreenTimeHelper = ScreenTimeHelper(),
    private val appsRepository: AppsRepository = AppsRepository(),
    private val usersRepository: UsersRepository = UsersRepository(),
) : ViewModel(){

    private val _profile = mutableStateOf(UserProfile())
    private val _totalScreenTimeState = mutableLongStateOf(0)
    private val _loadingState = mutableStateOf(true)
    private val _appsState = mutableStateOf(listOf<UserApps>())
    private val _iconsState = mutableStateOf(mapOf<String, String>())
    private lateinit var back : () -> Unit
    val appState : List<UserApps>
        get() = _appsState.value

    val iconState : Map<String, String>
        get() = _iconsState.value

    val loadingState : Boolean
        get() = _loadingState.value

    init {
        viewModelScope.launch {
            val uid = usersRepository.getProfileUID(profileId)
            _iconsState.value = appsRepository.getAppIcons(uid)
            _appsState.value = appsRepository.getApps(uid)
            _profile.value = usersRepository.getProfile(uid)

            val suggestions = appsRepository.getSuggestedAppRestriction(_profile.value.age.toInt())
            Log.w("SUGGESTIONS", suggestions.toString())

            withContext(Dispatchers.Default)  {
                _appsState.value.forEach {app ->
                    screenTimeHelper.addScreenTime(app.screenTime.toFloat())
                }
                _totalScreenTimeState.longValue = screenTimeHelper.getTotalScreenTime().toLong()
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
            async {
                appsRepository.updateAppRestriction(profileId, appName, newRestriction)
            }.await()
        }
    }

    fun getTotalScreenTime() : Float {
        return screenTimeHelper.getTotalScreenTime()
    }
}