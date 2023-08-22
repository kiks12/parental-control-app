package com.example.parental_control_app.data

import com.google.firebase.Timestamp

data class Sms(
    val messageBody: String,
    val originatingAddress: String,
    val timestamp: Timestamp,
)
