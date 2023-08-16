package com.example.parental_control_app.activities.children

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme

class ChildrenBlockedAppsActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParentalcontrolappTheme {
                Scaffold (
                    contentWindowInsets = WindowInsets.statusBars,
                    topBar = { TopAppBar(title = { Text("afasdf")}) },
                    content = {
                        Text("dasdfsdfsdf")
                    }
                )
            }
        }
    }
}