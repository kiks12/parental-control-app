package com.example.parental_control_app.repositories

import android.graphics.Bitmap
import android.util.Log
import com.example.parental_control_app.AppRestriction
import com.example.parental_control_app.data.UserAppIcon
import com.example.parental_control_app.data.UserApps
//import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

@Suppress("unused")
class AppsRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    suspend fun getApps(uid: String) : List<UserApps> {
        val completable = CompletableDeferred<List<UserApps>>()

        coroutineScope {
            launch(Dispatchers.IO) {
                val collection = db.collection("profiles/$uid/apps")
                val query = collection.get().await()
                val apps = query.toObjects(UserApps::class.java)
                completable.complete(apps)
            }
        }

        return completable.await()
    }

    suspend fun getBlockedApps(uid: String) : List<UserApps> {
        val completable = CompletableDeferred<List<UserApps>>()

        coroutineScope {
            launch(Dispatchers.IO) {
                val collection = db.collection("profiles/$uid/apps")
                val query = collection.whereEqualTo("restricted", true)
                val apps = query.get().await().toObjects(UserApps::class.java)
                completable.complete(apps)
            }
        }

        return completable.await()
    }

    suspend fun getAppIcons(uid: String, apps: List<UserApps>) : Map<String, String> {
        val icons  = CompletableDeferred<Map<String, String>>()
        val iconMap = mutableMapOf<String, String>()
        val ref = storage.reference

        coroutineScope {
            launch(Dispatchers.IO) {
                async {
                    val uidRef = ref.child(uid)
                    apps.forEach { app ->
                        val iconsPair = CompletableDeferred<Pair<String, String>>()
                        uidRef.child("${app.packageName}.png").downloadUrl
                            .addOnSuccessListener {
                                iconsPair.complete(Pair(app.packageName, it.toString()))
                            }
                            .addOnFailureListener {
                                iconsPair.complete(Pair(app.packageName, "NOT_FOUND"))
                            }
                        iconMap += iconsPair.await()
                    }
                }.await()
                async {
                    Log.w("APP ICONS", iconMap.toString())
                    icons.complete(iconMap)
                }.await()
            }
        }

        return icons.await()
    }

    suspend fun getAppNames(uid: String) : List<String> {
        val appNames = mutableListOf<String>()

        coroutineScope {
            launch(Dispatchers.IO) {
                val apps = db.collection("profiles/$uid/apps").get().await()
                apps.documents.forEach {app ->
                    appNames.add(app.data?.get("packageName").toString())
                }
            }
        }

        return appNames
    }

    suspend fun saveApps(uid: String, apps: List<UserApps>) : String? {
        val completableMessage = CompletableDeferred<String?>(null)
        val batch = db.batch()

        coroutineScope {
            launch(Dispatchers.IO) {
                val appNames = getAppNames(uid)
                async {
                    apps.forEach { app ->
                        if (!appNames.contains(app.packageName)) {
                            val collection = db.collection("profiles/$uid/apps").document()
                            batch.set(collection, app)
                        }
                    }
                }.await()
                async {
                    batch.commit()
                        .addOnSuccessListener {
//                            Log.w("SAVE APP", "SUCCESS")
                            completableMessage.complete("Your apps are saved on the cloud")
                        }
                        .addOnFailureListener {
//                            Log.w("SAVE APP ERROR", it.localizedMessage!!)
                            completableMessage.complete(it.localizedMessage)
                        }
                }.await()
            }
        }

        return completableMessage.await()
    }

    suspend fun saveAppIcons(uid: String, icons: List<UserAppIcon>) : String? {
        val completableMessage = CompletableDeferred<String?>(null)
        val ref = storage.reference

        coroutineScope {
            launch(Dispatchers.IO) {
                val appNames = getAppNames(uid)
                async {
                    icons.forEach {userAppIcon ->
                        if (!appNames.contains(userAppIcon.name).not()) {
                            val uidRef = ref.child("$uid/${userAppIcon.name}.png")
                            val bitmap = userAppIcon.icon
                            val byteArray = ByteArrayOutputStream()
                            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArray)
                            val data = byteArray.toByteArray()
                            uidRef.putBytes(data).await()
                        }
                    }
                }.await()
                async {
                    if (icons.size == ref.child(uid).listAll().await().items.size) {
                        completableMessage.complete("App icons are uploaded on the cloud")
                    } else {
                        completableMessage.complete("Error uploading app icons")
                    }
                }.await()
            }
        }

        return completableMessage.await()
    }

    suspend fun updateAppRestriction(uid: String, appName: String, newRestriction: Boolean) {
        coroutineScope {
            launch(Dispatchers.IO) {
                val query = db.collection("profiles/$uid/apps").whereEqualTo("packageName", appName)
                    .limit(1)
                val docs = query.get().await()
                db.collection("profiles/$uid/apps")
                    .document(docs.documents[0].id)
                    .update(
                        "restricted", newRestriction,
                        "limit", 0
                    ).await()
            }
        }
    }

    suspend fun updateAppScreenTimeLimit(uid: String, appName: String, newTimeLimit: Long) {
        coroutineScope {
            launch(Dispatchers.IO){
                val query = db.collection("profiles/$uid/apps").whereEqualTo("packageName", appName).limit(1)
                val docs = query.get().await()
                db.collection("profiles/$uid/apps")
                    .document(docs.documents[0].id)
                    .update(
                        "limit",
                        newTimeLimit
                    ).await()
            }
        }
    }

    suspend fun updateAppScreenTime(uid: String, apps: List<UserApps>) {
        val batch = db.batch()
        coroutineScope {
            launch(Dispatchers.IO) {
                async {
                    apps.forEach {app ->
                        val query = db.collection("profiles/$uid/apps")
                            .whereEqualTo("packageName", app.packageName).limit(1)
                        val docs = query.get().await()
                        docs.forEach { document ->
                            val ref = db.collection("profiles/$uid/apps")
                                .document(document.id)
                            batch.update(ref, "screenTime", app.screenTime)
                        }
                    }
                }.await()
                async { batch.commit() }.await()
            }
        }
    }



    /*
     * suggestions collection related methods
     */
    suspend fun saveAppRestrictions(list: List<AppRestriction>) {
        val batch = db.batch()
        coroutineScope {
            launch(Dispatchers.IO) {
                async {
                    list.forEach { appRestriction ->
                        val document = db.collection("suggestions").document()
                        batch.set(document, appRestriction)
                    }
                }.await()
                async {
                    batch.commit()
                }.await()
            }
        }
    }

    suspend fun getAppBlockingRecommendation(apps: List<UserApps>, contentRating: Int): List<String> {
        val recommendation = CompletableDeferred<List<String>>()
        val appNames = mutableListOf<String>()

        coroutineScope {
            launch(Dispatchers.IO){
                async {
                    apps.forEach { app ->
                        val query = db.collection("suggestions")
                            .whereEqualTo("packageName", app.packageName)
                            .whereLessThanOrEqualTo("contentRating", contentRating)
                        val docs = query.get().await()
                        docs.forEach { document ->
                            appNames.add(document.data["packageName"].toString())
                        }
                    }
                }.await()
                async {
                    recommendation.complete(appNames)
                }.await()
            }
        }

        return recommendation.await()
    }
    /*
     * suggestions collection related methods
     */
}