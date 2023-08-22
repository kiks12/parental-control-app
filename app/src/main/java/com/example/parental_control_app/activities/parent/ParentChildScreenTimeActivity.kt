package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.R
import com.example.parental_control_app.screens.parent.ParentChildScreenTimeScreen
import com.example.parental_control_app.viewmodels.parent.ParentChildScreenTimeViewModel

class ParentChildScreenTimeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parentChildScreenTimeViewModel = ParentChildScreenTimeViewModel()

        setContent {
            ParentChildScreenTimeScreen(parentChildScreenTimeViewModel)
        }
    }
}