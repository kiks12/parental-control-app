package com.example.parental_control_app.repositories.users


data class UserState(
    val userId: String,
    val email: String,
    val firstSignIn: Boolean = true,
)


data class UserProfile(
    val profileId: String,
    val name: String,
    val userId: String,
    val age: String = "",
    val birthday: Long = 0,
    val parent: Boolean = false,
    val child: Boolean = false,
    val phoneNumber: String? = null,
    val password: String = "",
    val maturityLevel: String? = null
) {
    constructor(): this("","","")
}

enum class UserMaturityLevel{
    BELOW_AVERAGE,
    AVERAGE,
    ABOVE_AVERAGE
}
