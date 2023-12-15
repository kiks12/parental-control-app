package com.example.parental_control_app.repositories.users

import com.example.parental_control_app.data.Response
import com.example.parental_control_app.data.ResponseStatus
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit


class UsersRepository {
    private val db = Firebase.firestore

    suspend fun createUser(newUser: UserState): String {
        val completable = CompletableDeferred<String>()

        coroutineScope {
            launch(Dispatchers.IO){
                async {
                    db.collection("users")
                        .document(newUser.userId)
                        .set(newUser, SetOptions.merge())
                        .addOnSuccessListener { completable.complete("Successfully registered an account") }
                        .addOnFailureListener { completable.complete(it.localizedMessage!!) }
                }.await()
            }
        }

        return completable.await()
    }

    suspend fun findUserProfiles(userId: String): List<UserProfile> {
        val completable = CompletableDeferred<List<UserProfile>>()

        coroutineScope {
            launch(Dispatchers.IO){
                async {
                    val query = db.collection("profiles")
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()
                    val docs = query.toObjects(UserProfile::class.java)
                    completable.complete(docs)
                }.await()
            }
        }

        return completable.await()
    }

    suspend fun findUserKidProfiles(userId: String): List<UserProfile> {
        val completable = CompletableDeferred<List<UserProfile>>()

        coroutineScope {
            launch(Dispatchers.IO){
                async {
                    val query = db.collection("profiles")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("child", true)
                        .get()
                        .await()
                    val docs = query.toObjects(UserProfile::class.java)
                    completable.complete(docs)
                }.await()
            }
        }

        return completable.await()
    }

    suspend fun findUserByEmail(email: String) : Response {
        val completable = CompletableDeferred<Response>()

        coroutineScope {
            launch(Dispatchers.IO) {
                db.collection("users").whereEqualTo("email", email).get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.isEmpty) {
                            completable.complete(
                                Response(
                                    ResponseStatus.SUCCESS,
                                    "Email is available",
                                    data = mapOf(
                                        "used" to false
                                    )
                                )
                            )
                        } else {
                            completable.complete(
                                Response(
                                    ResponseStatus.SUCCESS,
                                    "Email is used already",
                                    data = mapOf(
                                        "used" to true
                                    )
                                )
                            )
                        }
                    }
                    .addOnFailureListener {
                        it.localizedMessage?.let { it1 ->
                            Response(
                                ResponseStatus.FAILED,
                                it1
                            )
                        }?.let { it2 ->
                            completable.complete(
                                it2
                            )
                        }
                    }
            }
        }

        return completable.await()
    }

    suspend fun findUser(userId: String) : UserState {
        val completable = CompletableDeferred<UserState>()

        coroutineScope {
            launch(Dispatchers.IO){
                db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener {
                        val user = UserState(
                            userId = it.getString("userId")!!,
                            email = it.getString("email")!!,
                            firstSignIn = it.getBoolean("firstSignIn")!!,
                        )
                        completable.complete(user)
                    }
            }
        }

        return completable.await()
    }

    suspend fun deleteProfile(profile: UserProfile) : Response {
        val completable = CompletableDeferred<Response>()

        coroutineScope {
            launch(Dispatchers.IO){
                val uid = getProfileUID(profile.profileId)
                val docRef = db.collection("profiles").document(uid)
                docRef.delete()
                    .addOnSuccessListener {
                        completable.complete(
                            Response(
                                ResponseStatus.SUCCESS,
                                "Successfully deleted ${profile.name}"
                            )
                        )
                    }
                    .addOnFailureListener { it.localizedMessage?.let { it1 ->
                        completable.complete(
                            Response(
                                ResponseStatus.FAILED,
                                it1
                            )
                        )
                    } }
            }
        }

        return completable.await()
    }

    suspend fun saveProfile(profile: UserProfile) : Response {
        val completable = CompletableDeferred<Response>()

        coroutineScope {
            launch(Dispatchers.IO) {
                val docRef = db.collection("profiles").document()
                val query = docRef.set(profile)
                query
                    .addOnSuccessListener {
                        completable.complete(
                            Response(
                                ResponseStatus.SUCCESS,
                                "Successfully created new profile"
                            )
                        )
                    }
                    .addOnFailureListener { it.localizedMessage?.let { it1 ->
                        completable.complete(
                            Response(
                                ResponseStatus.FAILED,
                                it1
                            )
                        )
                    } }
            }
        }

        return completable.await()
    }

    suspend fun saveProfiles(profiles: List<UserProfile>) : String {
        val completable = CompletableDeferred<String>()

        coroutineScope {
            launch(Dispatchers.IO){
                val batch = db.batch()
                profiles.forEach {profile ->
                    val collection = db.collection("profiles").document()
                    batch.set(collection, profile)
                }
                batch.commit()
                    .addOnSuccessListener { completable.complete("Successfully created profiles") }
                    .addOnFailureListener { it.localizedMessage?.let { it1 ->
                        completable.complete(
                            it1
                        )
                    } }
            }
        }

        return completable.await()
    }

    suspend fun getProfileUID(profileId: String) : String {
        val uid = CompletableDeferred<String>()

        coroutineScope {
            launch(Dispatchers.IO){
                db.collection("profiles")
                    .whereEqualTo("profileId", profileId)
                    .get()
                    .addOnSuccessListener {
                        if (it.documents.isEmpty()) uid.complete("")
                        else uid.complete(it.documents[0].id)
                    }
                    .addOnFailureListener {  }
            }
        }

        return uid.await()
    }

    suspend fun getProfile(uid: String) : UserProfile {
        val completable = CompletableDeferred<UserProfile>()

        coroutineScope{
            launch(Dispatchers.IO){
                async {
                    val doc = db.collection("profiles").document(uid).get().await()
                    if (doc != null) {
                        completable.complete(doc.toObject(UserProfile::class.java)!!)
                    }
                }.await()
            }
        }

        return completable.await()
    }

    suspend fun updateFirstSignIn(userId: String) : String {
        val completable = CompletableDeferred<String>()

        coroutineScope {
            launch(Dispatchers.IO){
                db.collection("users")
                    .document(userId)
                    .update("firstSignIn", false)
                    .addOnSuccessListener { completable.complete("Successfully updated account") }
                    .addOnFailureListener { completable.complete(it.localizedMessage!!) }
            }
        }

        return completable.await()
    }

    suspend fun updateBlockStatus(uid: String, status: Boolean) {
        coroutineScope {
            launch(Dispatchers.IO){
                val ref = db.collection("profiles").document(uid)
                ref.update("blockChange", status).await()
            }
        }
    }

    suspend fun lockChildPhone(uid: String) {
        coroutineScope {
            launch(Dispatchers.IO){
                val ref = db.collection("profiles").document(uid)
                ref.update("phoneLock", true).await()
            }
        }
    }

    suspend fun unlockChildPhone(uid: String) {
        coroutineScope {
            launch(Dispatchers.IO){
                val ref = db.collection("profiles").document(uid)
                ref.update("phoneLock", false).await()
            }
        }
    }

    suspend fun setChildScreenTime(uid: String, screenTime: Long) : Response? {
        val responseCompletable = CompletableDeferred<Response?>(null)

        coroutineScope {
            launch(Dispatchers.IO){
                val ref = db.collection("profiles").document(uid)
                ref.update("phoneScreenTime", screenTime)
                    .addOnSuccessListener {
                        responseCompletable.complete(
                            Response(
                                status = ResponseStatus.SUCCESS,
                                message = "Successfully set screen time",
                            )
                        )
                    }
                    .addOnFailureListener {
                        responseCompletable.complete(
                            it.localizedMessage?.let { it1 ->
                                Response(
                                    status = ResponseStatus.FAILED,
                                    message = it1,
                                )
                            }
                        )
                    }
            }
        }

        return responseCompletable.await()
    }

    suspend fun getChildScreenTimeLimit(uid: String) : Response? {
        val responseCompletable = CompletableDeferred<Response?>(null)

        coroutineScope {
            launch(Dispatchers.IO){
                val ref = db.collection("profiles").document(uid)
                val document = ref.get().await()
                if (document.exists()) {
                    val limit = document.data?.get("phoneScreenTimeLimit").toString().toLong()
                    val hours = TimeUnit.MILLISECONDS.toHours(limit)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(limit) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(limit))
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(limit) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(limit))

                    responseCompletable.complete(
                        Response(
                            status = ResponseStatus.SUCCESS,
                            message = "Screen time limit was fetched successfully",
                            data = mapOf(
                                "HOURS" to hours,
                                "MINUTES" to minutes,
                                "SECONDS" to seconds
                            )
                        )
                    )
                } else {
                    responseCompletable.complete(
                        Response(
                            status = ResponseStatus.FAILED,
                            message = "Data not found"
                        )
                    )
                }
            }
        }

        return responseCompletable.await()
    }

    suspend fun setChildScreenTimeLimit(uid: String, limit: Long, clear: Boolean = false) : Response? {
        val responseCompletable = CompletableDeferred<Response?>(null)

        coroutineScope {
            launch(Dispatchers.IO) {
                val ref = db.collection("profiles").document(uid)
                ref.update("phoneScreenTimeLimit", limit)
                    .addOnSuccessListener {
                        if (clear) {
                            responseCompletable.complete(
                                Response(
                                    status = ResponseStatus.SUCCESS,
                                    message = "Successfully remove child screen time"
                                )
                            )
                        } else {
                            responseCompletable.complete(
                                Response(
                                    status = ResponseStatus.SUCCESS,
                                    message = "Successfully set child screen time"
                                )
                            )
                        }
                    }
                    .addOnFailureListener {
                        responseCompletable.complete(
                            it.localizedMessage?.let { it1 ->
                                Response(
                                    status = ResponseStatus.FAILED,
                                    message = it1
                                )
                            }
                        )
                    }
            }
        }

        return responseCompletable.await()
    }


    suspend fun saveUninstalledStatus(uid: String, status: Boolean) : Response? {
        val response = CompletableDeferred<Response?>(null)

        coroutineScope {
            launch(Dispatchers.IO) {
                val ref = db.collection("profiles").document(uid)
                val data = hashMapOf(
                    "uninstalled" to status
                )
                ref.set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        response.complete(
                            Response(
                                status = ResponseStatus.SUCCESS,
                                message = "Successfully saved uninstalled status"
                            )
                        )
                    }
                    .addOnFailureListener {
                        response.complete(
                            it.localizedMessage?.let { it1 ->
                                Response(
                                    status = ResponseStatus.FAILED,
                                    message = it1
                                )
                            }
                        )
                    }
            }
        }

        return response.await()
    }

    suspend fun saveDeviceModel(uid: String, deviceName: String) : Response? {
        val responseCompletable = CompletableDeferred<Response?>(null)

        coroutineScope {
            launch(Dispatchers.IO) {
                val ref = db.collection("profiles").document(uid)
                val data = hashMapOf(
                    "deviceName" to deviceName
                )
                ref.set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        responseCompletable.complete(
                            Response(
                                status = ResponseStatus.SUCCESS,
                                message = "Successfully saved device model"
                            )
                        )
                    }
                    .addOnFailureListener {
                        responseCompletable.complete(
                            it.localizedMessage?.let { it1 ->
                                Response(
                                    status = ResponseStatus.FAILED,
                                    message = it1
                                )
                            }
                        )
                    }
            }
        }

        return responseCompletable.await()
    }

    suspend fun saveProfileStatus(uid: String, status: Boolean) : Response? {
        val response = CompletableDeferred<Response?>(null)

        coroutineScope {
            launch(Dispatchers.IO){
                val ref = db.collection("profiles").document(uid)
                val data = hashMapOf(
                    "activeStatus" to status
                )

                ref.set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        response.complete(
                            Response(
                                status = ResponseStatus.SUCCESS,
                                message = "Successfully saved active status"
                            )
                        )
                    }
                    .addOnFailureListener {
                        response.complete(
                            it.localizedMessage?.let { it1 ->
                                Response(
                                    status = ResponseStatus.FAILED,
                                    message = it1
                                )
                            }
                        )
                    }
            }
        }

        return response.await()
    }

}