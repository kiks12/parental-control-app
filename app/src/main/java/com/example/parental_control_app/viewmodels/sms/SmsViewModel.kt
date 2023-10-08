package com.example.parental_control_app.viewmodels.sms

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.activities.sms.SmsMessageActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.repositories.SmsRepository
import com.example.parental_control_app.repositories.users.UserProfile
import kotlinx.coroutines.launch

class SmsViewModel(
    profile: UserProfile,
    private val kidProfileId: String,
    private val smsRepository: SmsRepository = SmsRepository(),
) : ViewModel() {

    private lateinit var activityStarter: ActivityStarterHelper

    private val _smsState = mutableStateOf<List<String>>(listOf())

    val profileState = profile

    val smsState : List<String>
        get() = _smsState.value

    init {
        viewModelScope.launch {
             _smsState.value = smsRepository.getProfileSms(kidProfileId)!!
        }
    }

    fun onSmsClick(documentId: String) {
        activityStarter.startNewActivity(
            activity = SmsMessageActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId,
                "sender" to documentId
            )
        )
    }

    fun setActivityStarterHelper(helper: ActivityStarterHelper) {
        activityStarter = helper
    }

}