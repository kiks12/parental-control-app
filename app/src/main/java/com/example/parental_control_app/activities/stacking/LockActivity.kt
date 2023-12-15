package com.example.parental_control_app.activities.stacking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LockActivity : AppCompatActivity() {

    private val usersRepository = UsersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val time = intent.getLongExtra("time", 0)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val uid = SharedPreferencesManager.getUID(sharedPreferences)
        var spTimer = SharedPreferencesManager.genTimer(sharedPreferences)

        if (spTimer == 0L) {
            val editor = sharedPreferences.edit()
            editor.putLong(SharedPreferencesManager.TIMER_KEY, time)
            editor.apply()
            spTimer = time
        }

        setContent {
            val scope = rememberCoroutineScope()
            var timerText by remember { mutableStateOf("") }

            val timer = object : CountDownTimer(spTimer,1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    val minutes =
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished - TimeUnit.HOURS.toMillis(hours))
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(
                        millisUntilFinished - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(
                            minutes
                        )
                    )

                    val formattedTime =
                        String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    timerText = formattedTime

                    val editor = sharedPreferences.edit()
                    editor.putLong(SharedPreferencesManager.TIMER_KEY, millisUntilFinished)
                    editor.apply()
                }

                override fun onFinish() {
                    timerText = "Finished!"
                    scope.launch {
                        usersRepository.unlockChildPhone(uid.toString())
                    }
                }
            }

            timer.start()
            ParentalControlAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ){
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Icon(Icons.Filled.Lock, "Phone Lock", modifier = Modifier.size(50.dp))
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "Time for a break!",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 60.sp
                        )
                        Spacer(Modifier.height(30.dp))
                        Text("Time Remaining: ")
                        Text(timerText, fontWeight = FontWeight.SemiBold, fontSize = 30.sp)
                        Spacer(Modifier.height(30.dp))
                        Text("Your phone is locked by your parent")
                    }
                }
            }
        }
    }
}
