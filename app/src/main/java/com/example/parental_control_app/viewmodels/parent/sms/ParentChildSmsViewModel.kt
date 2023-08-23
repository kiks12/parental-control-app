package com.example.parental_control_app.viewmodels.parent.sms

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.activities.parent.sms.ParentChildSmsMessageActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.repositories.SmsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ParentChildSmsViewModel(
    private val kidProfileId: String,
    private val smsRepository: SmsRepository = SmsRepository(),
) : ViewModel() {

    private lateinit var activityStarter: ActivityStarterHelper
    private lateinit  var onBackClick: () -> Unit

    private val _smsState = mutableStateOf<List<String>>(listOf())
    val smsState : List<String>
        get() = _smsState.value

    init {
        viewModelScope.launch {
            async { _smsState.value = smsRepository.getProfileSms(kidProfileId)!! }.await()
        }
    }

    fun addOnBackClick(callback: () -> Unit) {
        onBackClick = callback
    }

    fun getOnBackClick(): () -> Unit {
        return onBackClick
    }

    fun onSmsClick(documentId: String) {
        activityStarter.startNewActivity(
            activity = ParentChildSmsMessageActivity::class.java,
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