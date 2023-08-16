package com.example.parental_control_app.repositories

import android.content.pm.ApplicationInfo
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class AppsRepository {

    private val db = Firebase.firestore
    fun getApps(profileId: String) {
        var list = mutableListOf<ApplicationInfo>()
        db.collection("profiles")
            .whereEqualTo("profileId", profileId)
            .get()
            .addOnSuccessListener {
                it.documents.forEach {
                    val apps = it.data?.get("apps") as ArrayList<*>
                    Log.w("APPS", apps.size.toString())
                }
            }
            .addOnFailureListener { Log.w("GET APPS ERROR", it.localizedMessage) }
    }

    private suspend fun getProfileUID(profileId: String) : String {
        val uid = CompletableDeferred<String>()
        db.collection("profiles")
            .whereEqualTo("profileId", profileId)
            .get()
            .addOnSuccessListener {
                uid.complete(it.documents[0].id)
            }
            .addOnFailureListener {  }
        return uid.await()
    }

    suspend fun saveApps(profileId: String, apps: List<String>) {
        var uid : String? = null
        coroutineScope {
            async {
                uid = getProfileUID(profileId)
            }.await()
            async {
                if (uid != null) {
                    db.collection("profiles")
                        .document(uid!!)
                        .update("apps", apps)
                        .addOnSuccessListener {
                            Log.w("SAVE APP", "SUCCESS")
                        }
                        .addOnFailureListener {
                            Log.w("SAVE APP ERROR", it.localizedMessage)
                        }
                }
            }.await()
        }
    }
}