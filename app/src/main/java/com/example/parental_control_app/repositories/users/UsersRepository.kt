package com.example.parental_control_app.repositories.users

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class UsersRepository {
    private val db = Firebase.firestore

    suspend fun createUser(newUser: UserState): String {
        val completable = CompletableDeferred<String>()

        coroutineScope {
            launch(Dispatchers.IO){
                async {
                    db.collection("users")
                        .document(newUser.userId)
                        .set(newUser)
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
                    .addOnFailureListener { completable.complete(it.localizedMessage!!) }
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
}