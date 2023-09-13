package com.example.parental_control_app.repositories

import android.graphics.Bitmap
import android.util.Log
import com.example.parental_control_app.AppRestriction
import com.example.parental_control_app.data.UserAppIcon
import com.example.parental_control_app.data.UserApps
import com.example.parental_control_app.repositories.users.UsersRepository
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

class AppsRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val usersRepository = UsersRepository()

    suspend fun getApps(uid: String) : List<UserApps> {
        val completable = CompletableDeferred<List<UserApps>>()
//        var uid : String? = null

        coroutineScope  {
//            async { uid = usersRepository.getProfileUID(profileId) }.await()
            async {
                val collection = db.collection("profiles/$uid/apps")
                val query = collection.get().await()
                val apps = query.toObjects(UserApps::class.java)
                completable.complete(apps)
            }.await()
        }

        return completable.await()
    }

    suspend fun getBlockedApps(profileId: String) : List<UserApps> {
        val completable = CompletableDeferred<List<UserApps>>()
        var uid : String? = null

        coroutineScope {
            async { uid = usersRepository.getProfileUID(profileId) }.await()
            async {
                val collection = db.collection("profiles/$uid/apps")
                val query = collection.whereEqualTo("restricted", true)
                val apps = query.get().await().toObjects(UserApps::class.java)
                completable.complete(apps)
            }.await()
        }

        return completable.await()
    }

    suspend fun getAppIcons(uid: String) : Map<String, String> {
        val completable = CompletableDeferred<Map<String, String>>()
        var iconMap = mapOf<String, String>()
//        var uid : String? = null
        val ref = storage.reference

        coroutineScope {
//            async { uid = usersRepository.getProfileUID(profileId) }.await()
            async {
                val uidRef = ref.child(uid)
                val list = uidRef.listAll().await()
                list.items.forEach { storageReference ->
                    var name = storageReference.name
                    name = name.subSequence(0, name.length - 4).toString()
                    val imageUrl = storageReference.downloadUrl.await().toString()

                    iconMap = iconMap.plus(Pair(name, imageUrl))
                }
            }.await()
            async { completable.complete(iconMap) }.await()
        }

        return completable.await()
    }

    suspend fun getAppNames(uid: String) : List<String> {
        val completable = CompletableDeferred<List<String>>()
        var list = listOf<String>()

        coroutineScope {
            async {
                val apps = db.collection("profiles/$uid/apps").get().await()
                apps.documents.forEach {app ->
                    list = list.plus(app.data?.get("packageName").toString())
                }
            }.await()
            async {
                completable.complete(list)
            }.await()
        }

        return completable.await()
    }

    suspend fun getBlockedAppNames(uid: String) : List<String> {
        val completable = CompletableDeferred<List<String>>()
        var list = listOf<String>()

        coroutineScope {
            async {
                val apps = db.collection("profiles/$uid/apps")
                    .whereEqualTo("restricted", true)
                    .get().await()
                apps.documents.forEach {app ->
                    list = list.plus(app.data?.get("name").toString())
                }
            }.await()
            async {
                completable.complete(list)
            }.await()
        }

        return completable.await()
    }

    suspend fun saveApps(profileId: String, apps: List<UserApps>) {
        var appNames = listOf<String>()
        var uid : String? = null
        val batch = db.batch()

        coroutineScope {
            async { uid = usersRepository.getProfileUID(profileId) }.await()
            async { appNames = getAppNames(uid!!) }.await()
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
                        Log.w("SAVE APP", "SUCCESS")
                    }
                    .addOnFailureListener {
                        Log.w("SAVE APP ERROR", it.localizedMessage!!)
                    }
            }.await()
        }
    }

    suspend fun saveAppIcons(profileId: String, icons: List<UserAppIcon>) {
        var appNames = listOf<String>()
        var uid : String? = null
        val ref = storage.reference

        coroutineScope {
            async { uid = usersRepository.getProfileUID(profileId) }.await()
            async { appNames = getAppNames(uid!!) }.await()
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
        }
    }

    suspend fun updateAppRestriction(profileId: String, appName: String, newRestriction: Boolean) {
        var uid : String? = null

        coroutineScope {
            async { uid = usersRepository.getProfileUID(profileId) }.await()
            async {
                val query = db.collection("profiles/$uid/apps").whereEqualTo("packageName", appName)
                val docs = query.get().await()
                docs.documents.forEach { document ->
                    db.collection("profiles/$uid/apps")
                        .document(document.id)
                        .update("restricted", newRestriction).await()
                }
            }.await()
        }
    }

    suspend fun updateAppScreenTime(uid: String, apps: List<UserApps>) {
        val batch = db.batch()
        coroutineScope {
            async {
                apps.forEach {app ->
                    val query = db.collection("profiles/$uid/apps")
                        .whereEqualTo("packageName", app.packageName)
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

    suspend fun getSuggestedAppRestriction(age: Int) : List<String> {
        val completable = CompletableDeferred<List<String>>()
        var list = listOf<String>()

        coroutineScope {
            launch(Dispatchers.IO) {
                val query = db.collection("suggestions").whereGreaterThanOrEqualTo("age", age)
                val docs = query.get().await()
                docs.forEach {  document ->
                    list = list.plus(document.data["packageName"].toString())
                }
                completable.complete(list)
            }
        }

        return completable.await()
    }
    /*
     * suggestions collection related methods
     */
}