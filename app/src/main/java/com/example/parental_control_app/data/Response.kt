package com.example.parental_control_app.data

data class Response (
    val status: ResponseStatus,
    val message: String,
    val data: Map<Any, Any>? = null
)


enum class ResponseStatus {
    SUCCESS,
    FAILED,
}