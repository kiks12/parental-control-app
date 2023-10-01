package com.example.parental_control_app.activities.stacking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme

class BlockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ParentalControlAppTheme {
                BlockScreen()
            }
        }
    }
}

@Composable
private fun BlockScreen() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text("Oops!", fontSize = 75.sp, fontWeight = FontWeight.Bold)
            Text("This app is blocked by your parent")
        }
    }
}
