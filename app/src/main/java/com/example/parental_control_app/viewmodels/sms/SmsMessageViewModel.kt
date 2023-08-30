package com.example.parental_control_app.viewmodels.sms

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.Sms
import com.example.parental_control_app.repositories.SmsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SmsMessageViewModel(
    private val kidProfileId: String,
    private val sender: String,
    private val smsRepository: SmsRepository = SmsRepository()
) : ViewModel(){

    lateinit var onBackClick : () -> Unit
    private val _messagesState = mutableStateOf<List<Sms>>(listOf())
    val messagesState : List<Sms>
        get() = _messagesState.value

    init {
        viewModelScope.launch {
            async { _messagesState.value = smsRepository.getSmsMessages(kidProfileId, sender)!! }.await()
        }
    }

    fun addOnBackClick(callback: () -> Unit) {
        onBackClick = callback
    }

    fun getSender() : String {
        return sender
    }
}