package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme

class ParentMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParentalcontrolappTheme {
                Surface (
                    modifier = Modifier.fillMaxSize()
                ){
                    Text("Parent Activity")
                }
            }
        }
    }
}