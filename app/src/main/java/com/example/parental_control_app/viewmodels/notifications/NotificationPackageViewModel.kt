package com.example.parental_control_app.viewmodels.notifications

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.ReceivedNotification
import com.example.parental_control_app.repositories.NotificationsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NotificationPackageViewModel(
    private val profileId: String,
    private val packageName: String,
    private val notificationsRepository: NotificationsRepository = NotificationsRepository()
): ViewModel() {

    lateinit var onBackClick: () -> Unit
    private val _notificationsState = mutableStateOf<List<ReceivedNotification>>(listOf())
    val notificationsState : List<ReceivedNotification>
        get() = _notificationsState.value

    init {
        viewModelScope.launch {
            async { _notificationsState.value = notificationsRepository.getNotificationNotifs(profileId, packageName)!! }.await()
        }
    }

    fun getPackageName() : String {
        return packageName
    }

    fun addOnBackClick(callback: () -> Unit) {
        onBackClick = callback
    }
}