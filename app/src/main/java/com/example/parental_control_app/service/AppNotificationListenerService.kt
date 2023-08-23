package com.example.parental_control_app.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.parental_control_app.data.ReceivedNotification
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.repositories.NotificationsRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AppNotificationListenerService : NotificationListenerService() {

    private val notificationsRepository = NotificationsRepository()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        super.onNotificationPosted(statusBarNotification)

        val sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, 0)
        val profile = SharedPreferencesHelper.getProfile(sharedPreferences)

        if (statusBarNotification != null) {
            val packageName = statusBarNotification.packageName
            val notification = statusBarNotification.notification

            val newNotification = ReceivedNotification(
                packageName = packageName,
                title = notification.extras.getString("android.title").toString(),
                content = notification.extras.getString("android.text").toString(),
                timestamp = Timestamp.now()
            )

            GlobalScope.launch(Dispatchers.IO) {
                async { notificationsRepository.saveNotification(profile?.profileId!!, newNotification) }.await()
            }
        }

    }

    override fun onNotificationRemoved(statusBarNotification: StatusBarNotification?) {
        super.onNotificationRemoved(statusBarNotification)
        // Handle the removed notification here
    }
}