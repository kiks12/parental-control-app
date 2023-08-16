package com.example.parental_control_app.viewmodels.parent

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ParentHomeViewModel(
    private val usersRepository: UsersRepository = UsersRepository(),
    private val appRepository: AppsRepository = AppsRepository()
) : ViewModel() {

    private val auth = Firebase.auth
    private var currentUser : FirebaseUser? = null
    var kidsProfile = mutableStateListOf<UserProfile>()

    init {
        currentUser = auth.currentUser
        viewModelScope.launch {
            async {
                kidsProfile.clear()
                val list = usersRepository.findUserKidProfiles(currentUser?.uid!!)
                kidsProfile.addAll(list)
                Log.w("KID PROFILE LIST", kidsProfile.toString())
            }.await()
        }
    }

}