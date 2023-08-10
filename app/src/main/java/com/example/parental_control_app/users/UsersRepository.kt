package com.example.parental_control_app.users

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred

class UsersRepository {
    private val db = Firebase.firestore

    suspend fun createUser(newUser: UserState): String {
        val completable = CompletableDeferred<String>()
        db.collection("users")
            .document(newUser.userId)
            .set(newUser)
            .addOnSuccessListener { completable.complete("Successfully registered an account") }
            .addOnFailureListener {completable.complete("Error")  }
        return completable.await()
    }

    fun findUser(userId: String) {
        db.collection("users")
            .get()
            .addOnSuccessListener {  }
    }
}