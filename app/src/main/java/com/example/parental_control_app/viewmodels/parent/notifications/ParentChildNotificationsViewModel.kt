package com.example.parental_control_app.viewmodels.parent.notifications

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.activities.parent.notifications.ParentChildNotificationsPackageActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.repositories.NotificationsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ParentChildNotificationsViewModel(
    private val kidProfileId: String,
    private val notificationsRepository: NotificationsRepository = NotificationsRepository(),
) : ViewModel(){

    private lateinit var onBackClick: () -> Unit
    private lateinit var activityStarter: ActivityStarterHelper

    private val _notificationState = mutableStateOf<List<String>>(listOf())
    val notificationState : List<String>
        get() = _notificationState.value

    init {
        viewModelScope.launch {
            async { _notificationState.value = notificationsRepository.getProfileNotifications(kidProfileId)!! }.await()
        }
    }

    fun addOnBackClick(callback: () -> Unit) {
        onBackClick = callback
    }

    fun getOnBackClick() : () -> Unit {
        return onBackClick
    }

    fun setActivityStarterHelper(helper: ActivityStarterHelper) {
        activityStarter = helper
    }

    fun onNotificationClick(packageName: String) {
        activityStarter.startNewActivity(
            activity = ParentChildNotificationsPackageActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId,
                "packageName" to packageName
            )
        )
    }
}