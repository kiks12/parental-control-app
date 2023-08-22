package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.screens.parent.ParentChildSmsScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.parent.ParentChildSmsViewModel

class ParentChildSmsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parentChildSmsViewModel = ParentChildSmsViewModel()

        setContent {
            ParentalcontrolappTheme {
                ParentChildSmsScreen(parentChildSmsViewModel)
            }
        }

        TODO("Parent Child Sms Activity - Get Kid Profile ID")
    }
}