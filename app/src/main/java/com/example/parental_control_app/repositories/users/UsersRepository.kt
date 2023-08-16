package com.example.parental_control_app.repositories.users

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class UsersRepository {
    private val db = Firebase.firestore

    suspend fun createUser(newUser: UserState): String {
        val completable = CompletableDeferred<String>()
        db.collection("users")
            .document(newUser.userId)
            .set(newUser)
            .addOnSuccessListener { completable.complete("Successfully registered an account") }
            .addOnFailureListener { completable.complete(it.localizedMessage.toString()) }
        return completable.await()
    }

    suspend fun findUserProfiles(userId: String): List<UserProfile> {
        val completable = CompletableDeferred<List<UserProfile>>()
        var list = listOf<UserProfile>()
        val query = db.collection("profiles")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        runBlocking {
            val job : List<Job> = (0 until query.documents.size).map { index ->
                launch {
                    val doc = query.documents[index]
                    list = list.plus(
                        UserProfile(
                        profileId = doc.data?.get("profileId").toString(),
                        name = doc.data?.get("name").toString(),
                        userId = doc.data?.get("userId").toString(),
                        phoneNumber = doc.data?.get("phoneNumber").toString(),
                        parent = doc.data?.get("parent") as Boolean,
                        child = doc.data?.get("child") as Boolean,
                        password = doc.data?.get("password").toString(),
                    )
                    )
                }
            }
            val jobTwo : Job = launch {
                completable.complete(list)
            }
            job.joinAll()
            jobTwo.join()
        }

        return completable.await()
    }

    suspend fun findUser(userId: String) : UserState {
        val completable = CompletableDeferred<UserState>()
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                val user = UserState(
                    userId = it.getString("userId")!!,
                    email = it.getString("email")!!,
                    isFirstSignIn = it.getBoolean("isFirstSignIn")!!,
                )
                completable.complete(user)
            }

        return completable.await()
    }

    suspend fun saveProfiles(profiles: List<UserProfile>) : String {
        val completable = CompletableDeferred<String>()
        val batch = db.batch()
        profiles.forEach {profile ->
            val collection = db.collection("profiles").document()
            batch.set(collection, profile)
        }
        batch.commit()
            .addOnSuccessListener { completable.complete("Successfully created profiles") }
            .addOnFailureListener { completable.complete(it.localizedMessage.toString()) }
        return completable.await()
    }

    suspend fun updateFirstSignIn(userId: String) : String {
        val completable = CompletableDeferred<String>()
        db.collection("users")
            .document(userId)
            .update("isFirstSignIn", false)
            .addOnSuccessListener { completable.complete("Successfully updated account") }
            .addOnFailureListener { completable.complete(it.localizedMessage.toString()) }
        return completable.await()
    }
}