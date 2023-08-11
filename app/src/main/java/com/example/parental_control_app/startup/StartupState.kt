package com.example.parental_control_app.startup

import com.example.parental_control_app.users.UserProfile
import com.example.parental_control_app.users.UserState

data class StartupState(
    val user: UserState = UserState("", ""),
    val profiles: List<UserProfile> = emptyList(),
    val profileInput: UserProfile = UserProfile("","", "")
)
