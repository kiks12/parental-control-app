package com.example.parental_control_app.repositories

import android.util.Log
import com.example.parental_control_app.data.Sms
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

class SmsRepository {

    private val usersRepository = UsersRepository()
    private val db = Firebase.firestore

    suspend fun getProfileSms(profileId: String) : List<String>? {
        val completable = CompletableDeferred<List<String>>()
        var uid = ""
        var list = listOf<String>()

        if (profileId.isEmpty()) return null

        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId) }.await()
                async {
                    val reference = db.collection("profiles/$uid/sms")
                    val docs = reference.get().await()
                    docs.documents.forEach {document ->
                        Log.w("MESSAGE DOC ID", document.id)
                        list = list.plus(document.id)
                    }
                }.await()
                async { completable.complete(list) }.await()
            }
        }

        return completable.await()
    }

    suspend fun getSmsMessages(profileId: String, sender: String) : List<Sms>? {
        var uid = ""
        val completable = CompletableDeferred<List<Sms>>()
        var list = listOf<Sms>()

        if (profileId.isEmpty()) return null

        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId) }.await()
                async {
                    val reference = db.collection("profiles/$uid/sms/$sender/messages")
                    val docs = reference.get().await()
                    docs.documents.forEach { document ->
                        val sms = Sms(
                            messageBody = document.data?.get("messageBody").toString(),
                            originatingAddress = document.data?.get("originatingAddress").toString(),
                            timestamp = document.data?.get("timestamp") as Timestamp,
                        )
                        list = list.plus(sms)
                    }
                }.await()
                async { completable.complete(list) }.await()
            }
        }

        return completable.await()
    }

    suspend fun saveSms(profileId: String, sms: Sms){
        var uid = ""
        val dummyData = mapOf(
            "dummy" to "data"
        )

        if (profileId.isEmpty()) return

        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId)}.await()
                async {
                    val reference = db.collection("profiles/$uid/sms").document(sms.originatingAddress)
                    reference.set(dummyData).await()
                    reference.collection("messages").document().set(sms).await()
                }.await()
            }
        }
    }

}