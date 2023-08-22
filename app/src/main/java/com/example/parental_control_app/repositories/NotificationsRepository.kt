package com.example.parental_control_app.repositories

import com.example.parental_control_app.data.ReceivedNotification
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class NotificationsRepository {

    private val usersRepository = UsersRepository()
    private val db = Firebase.firestore

    fun getProfileNotifications() {
        TODO("Notifications Repository - Implement getProfileNotifications")
    }

    suspend fun saveNotification(profileId: String, notification: ReceivedNotification) {
        var uid = ""
        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId) }.await()
                async {
                    val reference = db.collection("profiles/$uid/notifications").document(notification.packageName)
                    reference.set(notification)
                }.await()
            }
        }
    }
}