package com.example.parental_control_app.repositories.users

data class UserState(
    val userId: String,
    val email: String,
    val isFirstSignIn: Boolean = true,
)
data class UserProfile(
    val profileId: String,
    val name: String,
    val userId: String,
    val parent: Boolean = false,
    val child: Boolean = false,
    val apps: List<AppState>? = null,
    val restricted: List<AppState>? = null,
    val phoneNumber: String? = null,
    val password: String? = null,
)
//data class ParentState(
//    val parentId: String,
//    val name: String,
//    val phoneNumber: String? = null,
//    val mpin: String? = null,
//)
//data class ChildState(
//    val childId: String,
//    val name: String,
//    val apps: List<AppState>,
//    val restricted: List<AppState>,
//    val phoneNumber: String? = null,
//)
data class AppState(
    val appId: String,
    val name: String,
    val packageName: String,
)