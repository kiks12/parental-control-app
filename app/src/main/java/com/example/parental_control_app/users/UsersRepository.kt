package com.example.parental_control_app.users

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UsersRepository {
    private val db = Firebase.firestore

    fun findUser(userId: String) {
        db.collection("users")
            .get()
            .addOnSuccessListener {  }
    }
}