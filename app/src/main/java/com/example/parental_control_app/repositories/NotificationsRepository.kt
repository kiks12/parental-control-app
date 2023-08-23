package com.example.parental_control_app.repositories

import com.example.parental_control_app.data.ReceivedNotification
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationsRepository {

    private val usersRepository = UsersRepository()
    private val db = Firebase.firestore

    suspend fun getProfileNotifications(profileId: String) : List<String>? {
        var uid = ""
        val completable = CompletableDeferred<List<String>>()
        var list = listOf<String>()

        if (profileId.isEmpty()) return null

        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId) }.await()
                async {
                    val reference = db.collection("profiles/$uid/notifications")
                    val docs = reference.get().await()
                    docs.forEach { document ->
                        list = list.plus(document.id)
                    }
                }.await()
                async { completable.complete(list) }.await()
            }
        }

        return completable.await()
    }

    suspend fun getNotificationNotifs(profileId: String, packageName: String) : List<ReceivedNotification>? {
        var uid = ""
        val completable = CompletableDeferred<List<ReceivedNotification>>()
        var list = listOf<ReceivedNotification>()

        if (profileId.isEmpty()) return null

        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId) }.await()
                async {
                    val reference = db.collection("profiles/$uid/notifications/$packageName/notifs")
                    val docs = reference.get().await()
                    docs.documents.forEach { document ->
                        list = list.plus(
                            ReceivedNotification(
                                packageName = document.data?.get("packageName").toString(),
                                title = document.data?.get("title").toString(),
                                content = document.data?.get("content").toString(),
                                timestamp = document.data?.get("timestamp") as Timestamp
                            )
                        )
                    }
                }.await()
                async { completable.complete(list) }.await()
            }
        }

        return completable.await()
    }

    suspend fun saveNotification(profileId: String, notification: ReceivedNotification) {
        var uid = ""

        if (profileId.isEmpty()) return

        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId) }.await()
                async {
                    val reference = db.collection("profiles/$uid/notifications").document(notification.packageName)
                    val document = reference.collection("notifs").document()
                    document.set(notification).await()
                }.await()
            }
        }
    }
}