package com.example.parental_control_app.viewmodels.notifications

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.activities.notifications.NotificationPackageActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.repositories.NotificationsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val kidProfileId: String,
    private val activityStarterHelper: ActivityStarterHelper,
    private val notificationsRepository: NotificationsRepository = NotificationsRepository(),
) : ViewModel(){

    private val _notificationState = mutableStateOf<List<String>>(listOf())

    val notificationState : List<String>
        get() = _notificationState.value

    init {
        viewModelScope.launch {
            async { _notificationState.value = notificationsRepository.getProfileNotifications(kidProfileId)!! }.await()
        }
    }

    fun onNotificationClick(packageName: String) {
        activityStarterHelper.startNewActivity(
            activity = NotificationPackageActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId,
                "packageName" to packageName
            )
        )
    }
}