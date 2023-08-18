package com.example.parental_control_app.repositories.users

import com.example.parental_control_app.data.UserApps

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
    val apps: List<UserApps> = listOf(),
    val phoneNumber: String? = null,
    val password: String? = null,
    val maturityLevel: String? = null
) {
    constructor(): this("","","")
}

enum class UserMaturityLevel{
    BELOW_AVERAGE,
    AVERAGE,
    ABOVE_AVERAGE
}

//data class AppState(
//    val appId: String,
//    val name: String,
//    val packageName: String,
//)