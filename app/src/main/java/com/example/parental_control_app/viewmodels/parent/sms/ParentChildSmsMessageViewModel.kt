package com.example.parental_control_app.viewmodels.parent.sms

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.Sms
import com.example.parental_control_app.repositories.SmsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ParentChildSmsMessageViewModel(
    private val kidProfileId: String,
    private val sender: String,
    private val smsRepository: SmsRepository = SmsRepository()
) : ViewModel(){

    private val _messagesState = mutableStateOf<List<Sms>>(listOf())
    val messagesState : List<Sms>
        get() = _messagesState.value

    init {
        viewModelScope.launch {
            async { _messagesState.value = smsRepository.getSmsMessages(kidProfileId, sender)!! }.await()
        }
    }
}