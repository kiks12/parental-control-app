package com.example.parental_control_app.repositories.users


data class UserState(
    val userId: String,
    val email: String,
    val firstSignIn: Boolean = true,
)

data class UserProfile(
    var profileId: String,
    val name: String,
    var userId: String,
    val age: String = "",
    val birthday: Long = 0,
    val parent: Boolean = false,
    val child: Boolean = false,
    val phoneNumber: String? = null,
    var password: String = "",
    var maturityLevel: String? = null,
    val phoneLock: Boolean = false,
    val hide: Boolean = false,
    val blockChange: Boolean = false,
    val phoneScreenTime: Long = 0,
    val phoneScreenTimeLimit: Long = 0,
) {
    constructor(): this("","","")
}

enum class UserMaturityLevel{
    BELOW_AVERAGE,
    AVERAGE,
    ABOVE_AVERAGE
}
