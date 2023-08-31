package com.example.parental_control_app.repositories

import android.util.Log
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tomtom.sdk.location.GeoPoint
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LocationRepository {

    private val usersRepository = UsersRepository()
    private val db = Firebase.firestore

    suspend fun getProfileLocation(profileId: String) : GeoPoint {
        var uid = ""
        val completable = CompletableDeferred<GeoPoint>()

        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId) }.await()
                async {
                    val doc = db.collection("profiles/$uid/location").document("loc").get().await()
                    val point = GeoPoint(
                        doc.data?.get("latitude").toString().toDouble(),
                        doc.data?.get("longitude").toString().toDouble(),
                    )
                    completable.complete(point)
                }.await()
            }
        }

        return completable.await()
    }

    suspend fun saveLocation(profileId: String, point: GeoPoint) {
        var uid = ""

        coroutineScope {
            launch(Dispatchers.IO) {
                async { uid = usersRepository.getProfileUID(profileId) }.await()
                async {
                    val reference = db.collection("profiles/$uid/location").document("loc")
                    reference.set(point).await()
                }.await()
            }
        }
    }
}