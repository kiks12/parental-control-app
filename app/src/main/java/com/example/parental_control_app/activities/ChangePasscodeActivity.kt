package com.example.parental_control_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.Digits
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme

class ChangePasscodeActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toastHelper = ToastHelper(this)
        val sharedPreferences = applicationContext.getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val pin = SharedPreferencesManager.getPIN(sharedPreferences)

        setContent {

            var confirmed by remember { mutableStateOf(false) }
            var index by remember { mutableIntStateOf(1) }
            var firstNumber by remember { mutableStateOf("") }
            var secondNumber by remember { mutableStateOf("") }
            var thirdNumber by remember { mutableStateOf("") }
            var fourthNumber by remember { mutableStateOf("") }

            fun resetNumbers() {
                firstNumber = ""
                secondNumber = ""
                thirdNumber = ""
                fourthNumber = ""
                index = 1
            }

            fun onDigitClickWithoutSave(digit: String) {
                if (index <= 4) {
                    when (index) {
                        1 -> firstNumber = digit
                        2 -> secondNumber = digit
                        3 -> thirdNumber = digit
                        4 -> fourthNumber = digit
                        else -> {}
                    }
                    index += 1
                }

                if (index == 5) {
                    if ("$firstNumber$secondNumber$thirdNumber$fourthNumber" == pin) {
                        confirmed = true
                        resetNumbers()
                    } else {
                        toastHelper.makeToast("Incorrect PIN")
                        resetNumbers()
                    }
                }
            }

            fun onDigitClickWithSave(digit: String) {
                if (index <= 4) {
                    when (index) {
                        1 -> firstNumber = digit
                        2 -> secondNumber = digit
                        3 -> thirdNumber = digit
                        4 -> fourthNumber = digit
                        else -> {}
                    }
                    index += 1
                }
            }

            fun savePIN() {
                val edit = sharedPreferences.edit()
                edit.putString(SharedPreferencesManager.PIN_KEY, "$firstNumber$secondNumber$thirdNumber$fourthNumber")
                edit.apply()
                toastHelper.makeToast("Successfully changed passcode")
                finish()
            }

            fun onDelDigit() {
                index = if (index == 1) {
                    1
                } else {
                    index - 1
                }

                when (index) {
                    1 -> firstNumber = ""
                    2 -> secondNumber = ""
                    3 -> thirdNumber = ""
                    4 -> fourthNumber = ""
                }
            }

            ParentalControlAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Change Passcode")},
                            navigationIcon = { IconButton(onClick = { finish() }) {
                                Icon(Icons.Rounded.ArrowBack, "Go Back")
                            } }
                        )
                    }
                ){ innerPadding ->
                    Box(modifier=Modifier.padding(innerPadding)) {
                        AnimatedVisibility(visible = confirmed) {
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween){
                                Column(modifier=Modifier.padding(horizontal=30.dp, vertical=20.dp)){
                                    Text(text = "4-digit PIN", fontSize=30.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "Enter New PIN")
                                }
                                Column {
                                    Digits(
                                        firstNumber = firstNumber,
                                        secondNumber = secondNumber,
                                        thirdNumber = thirdNumber,
                                        fourthNumber = fourthNumber,
                                        index = index,
                                        onDigitClick = ::onDigitClickWithSave,
                                        onDelDigit = ::onDelDigit
                                    )

                                    Button(onClick = ::savePIN, modifier= Modifier
                                        .fillMaxWidth()
                                        .padding(30.dp)) {
                                        Text("Change Passcode", modifier = Modifier.padding(10.dp))
                                    }
                                }
                            }
                        }
                        AnimatedVisibility(visible = confirmed.not()) {
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween){
                                Column(modifier=Modifier.padding(horizontal=30.dp, vertical=20.dp)){
                                    Text(text = "4-digit PIN", fontSize=30.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "Enter Current PIN")
                                }
                                Column {
                                    Digits(
                                        firstNumber = firstNumber,
                                        secondNumber = secondNumber,
                                        thirdNumber = thirdNumber,
                                        fourthNumber = fourthNumber,
                                        index = index,
                                        onDigitClick = ::onDigitClickWithoutSave,
                                        onDelDigit = ::onDelDigit
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}