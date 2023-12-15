package com.example.parental_control_app.activities.stacking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme

class UnlockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(SharedPreferencesManager.TIMER_KEY)
        editor.apply()

        setContent {
            ParentalControlAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ){
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Icon(Icons.Outlined.Lock, "Phone Lock", modifier = Modifier.size(50.dp))
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "Phone Unlocked!",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 60.sp
                        )
                        Spacer(Modifier.height(30.dp))
                        Text("Your phone is unlocked by your parent")
                        Spacer(Modifier.height(50.dp))
                        Button(onClick = {
                            val homeIntent = Intent(Intent.ACTION_MAIN)
                            homeIntent.addCategory(Intent.CATEGORY_HOME)
                            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(homeIntent)
                            finish()
                        }) {
                            Text("Home")
                        }
                    }
                }
            }
        }
    }
}