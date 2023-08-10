package com.example.parental_control_app.users

data class UserState(
    val userId: String,
    val email: String,
    val parents: List<ParentState>,
    val children: List<ChildState>
)
data class ParentState(
    val parentId: String,
    val name: String,
)
data class ChildState(
    val childId: String,
    val name: String,
    val apps: List<AppState>,
    val restricted: List<AppState>,
)
data class AppState(
    val appId: String,
    val name: String,
    val packageName: String,
)