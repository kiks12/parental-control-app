package com.example.parental_control_app.activities.parent.sms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.parental_control_app.screens.parent.sms.ParentChildSmsMessageScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.parent.sms.ParentChildSmsMessageViewModel

class ParentChildSmsMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val sender = intent.getStringExtra("sender")
        val parentChildSmsMessageViewModel = ParentChildSmsMessageViewModel(kidProfileId!!, sender!!)


        setContent {
            ParentalcontrolappTheme {

                val messages = parentChildSmsMessageViewModel.messagesState

                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ){
                        ParentChildSmsMessageScreen(messages)
                    }
                }
            }
        }
    }
}