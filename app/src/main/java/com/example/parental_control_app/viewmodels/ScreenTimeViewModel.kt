package com.example.parental_control_app.viewmodels

import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.helpers.ScreenTimeHelper
import com.example.parental_control_app.repositories.AppsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScreenTimeViewModel(
    private val profileId: String,
    private val screenTimeHelper : ScreenTimeHelper = ScreenTimeHelper(),
    private val appsRepository: AppsRepository = AppsRepository()
) : ViewModel(){

    private val _totalScreenTimeState = mutableLongStateOf(0)
    private val _loadingState = mutableStateOf(true)
    private val _appsState = mutableStateOf(listOf<UserApps>())
    private val _iconsState = mutableStateOf(mapOf<String, String>())
    lateinit var onBackClick : () -> Unit

    val appState : List<UserApps>
        get() = _appsState.value

    val iconState : Map<String, String>
        get() = _iconsState.value

    val loadingState : Boolean
        get() = _loadingState.value

    val totalScreenTimeState : Long
        get() = _totalScreenTimeState.longValue

    init {
        viewModelScope.launch {
            async { _iconsState.value = appsRepository.getAppIcons(profileId) }.await()
            async { _appsState.value = appsRepository.getApps(profileId) }.await()
            async {
                _appsState.value = _appsState.value.sortedBy { app -> app.screenTime }
            }.await()
            async {
                _appsState.value.forEach {app ->
                    screenTimeHelper.addScreenTime(app.screenTime.toFloat())
                }
            }.await()
            async { _totalScreenTimeState.longValue = screenTimeHelper.getTotalScreenTime().toLong() }.await()
            async { _loadingState.value = false }.await()
        }
    }

    fun addOnBackClick(callback: () -> Unit) {
        onBackClick = callback
    }
}