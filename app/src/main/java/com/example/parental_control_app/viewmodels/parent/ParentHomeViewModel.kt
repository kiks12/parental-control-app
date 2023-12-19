package com.example.parental_control_app.viewmodels.parent

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ParentHomeViewModel(
    profile: UserProfile,
    private val usersRepository: UsersRepository = UsersRepository(),
) : ViewModel() {

    private val auth = Firebase.auth
    private var currentUser : FirebaseUser? = null
    private lateinit var onChildrenCardClick : (profileId: String) -> Unit
    private val _kidsProfileState = mutableStateListOf<UserProfile>()
    private val _loadingState = mutableStateOf(true)

    val profileState = profile
    val kidsProfileState : List<UserProfile>
        get() = _kidsProfileState
    val loadingState : Boolean
        get() = _loadingState.value

    init {
        currentUser = auth.currentUser
        viewModelScope.launch {
            _loadingState.value = true
            _kidsProfileState.clear()
            val list = usersRepository.findUserKidProfiles(currentUser?.uid!!)
            list.forEach { profile ->
                val childUID = usersRepository.getProfileUID(profile.profileId)
                usersRepository.saveUninstalledStatus(childUID, true)
                usersRepository.saveProfileStatus(childUID, false)
            }
            _kidsProfileState.addAll(list)
            async { _loadingState.value = false }.await()
        }
    }

    fun addOnChildrenCardClick(callback: (profileId: String) -> Unit) {
        onChildrenCardClick = callback
    }

    fun getOnChildrenCardClick(): (profileId: String) -> Unit {
        return onChildrenCardClick
    }

}