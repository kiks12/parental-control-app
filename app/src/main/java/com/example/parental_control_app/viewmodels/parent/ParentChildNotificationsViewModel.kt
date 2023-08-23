package com.example.parental_control_app.viewmodels.parent

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.ReceivedNotification
import com.example.parental_control_app.repositories.NotificationsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ParentChildNotificationsViewModel(
    private val kidProfileId: String,
    private val notificationsRepository: NotificationsRepository = NotificationsRepository(),
) : ViewModel(){

    private lateinit var onBackClick: () -> Unit
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

    fun onNotificationClick(packageName: String) {
        TODO("ParentChildNotificationsViewModel - start new activity and show all notifications of clicked package i.e. com.google.youtube")
    }
}