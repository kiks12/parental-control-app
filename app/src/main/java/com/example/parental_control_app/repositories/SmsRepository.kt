package com.example.parental_control_app.repositories

import com.example.parental_control_app.data.Sms
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SmsRepository {

    private val usersRepository = UsersRepository()
    private val db = Firebase.firestore

    suspend fun getProfileSms(profileId: String) {
        var uid = ""
        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId) }.await()
                async {
                    val reference = db.collection("sms")
                }.await()
            }
        }
        TODO("Sms Repository - Implement getProfileSms")
    }

    suspend fun saveSms(profileId: String, sms: Sms){
        var uid = ""
        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId)}.await()
                async {
                    val reference = db.collection("profiles/$uid/sms").document(sms.originatingAddress)
                    reference.set(sms)
                }.await()
            }
        }
    }

}