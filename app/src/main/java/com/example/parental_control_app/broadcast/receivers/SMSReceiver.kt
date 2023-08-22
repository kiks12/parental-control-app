package com.example.parental_control_app.broadcast.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.parental_control_app.data.Sms
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.repositories.SmsRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SMSReceiver : BroadcastReceiver(){

    private val smsRepository = SmsRepository()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val sharedPreferences = context.getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, 0)
            val profile = SharedPreferencesHelper.getProfile(sharedPreferences)

            for (sms in smsMessages) {
                val messageBody = sms.messageBody
                val senderPhoneNumber = sms.displayOriginatingAddress

                val newSms = Sms(
                    originatingAddress = sms.displayOriginatingAddress,
                    messageBody = sms.messageBody,
                    timestamp = Timestamp.now()
                )

                Log.w("MESSAGE BODY", messageBody)
                Log.w("MESSAGE PHONE NUMBER", senderPhoneNumber)

                GlobalScope.launch(Dispatchers.IO) {
                    async { smsRepository.saveSms(profile?.profileId!!, newSms) }.await()
                }
            }
        }
    }
}