package com.example.parental_control_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.activities.LoginActivity
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme

@Suppress("unused")
data class AppRestriction(
    val label: String,
    val packageName: String,
    val age: Int,
) {
    constructor() : this("", "", 0)
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toastHelper = ToastHelper(this)
        val sharedPreferences = applicationContext.getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val pin = SharedPreferencesManager.getPIN(sharedPreferences)
//        val edit = sharedPreferences.edit()
//        edit.remove(SharedPreferencesManager.PIN_KEY)
//        edit.apply()

        setContent {
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
                        startLoginActivity()
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
                startLoginActivity()
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
                if (!(pin.isNullOrEmpty()) || !(pin?.isBlank()!!)) {
                    Column(verticalArrangement = Arrangement.SpaceBetween, modifier=Modifier.fillMaxSize()){
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 20.dp,
                                vertical = 10.dp
                            )
                        ) {
                            Text(
                                "KidsGuard",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text("Enter 4-digit PIN to continue")
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
                } else {
                    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()){

                        Column(
                            modifier = Modifier.padding(
                                horizontal = 20.dp,
                                vertical = 10.dp
                            )
                        ) {
                            Text(
                                "KidsGuard",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                "Create PIN",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text("Please provide a 4-digit PIN to continue")
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

                            Spacer(modifier = Modifier.height(15.dp))

                            Button(onClick = ::savePIN, modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)) {
                                Text(text = "Save PIN", modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}

@Composable
fun Digits(
    firstNumber: String,
    secondNumber: String,
    thirdNumber: String,
    fourthNumber: String,
    index: Int,
    onDigitClick: (digit: String) -> Unit,
    onDelDigit: () -> Unit
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        OutlinedTextField(
            value = firstNumber,
            onValueChange = { },
            readOnly = true,
            enabled = false,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .size(55.dp)
                .border(
                    if (index == 1) 1.dp else 0.2.dp,
                    MaterialTheme.colorScheme.secondaryContainer
                )
        )
        OutlinedTextField(
            value = secondNumber,
            onValueChange = { },
            readOnly = true,
            enabled = false,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .size(55.dp)
                .border(
                    if (index == 2) 1.dp else 0.2.dp,
                    MaterialTheme.colorScheme.secondaryContainer
                )
        )
        OutlinedTextField(
            value = thirdNumber,
            onValueChange = { },
            readOnly = true,
            enabled = false,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .size(55.dp)
                .border(
                    if (index == 3) 1.dp else 0.2.dp,
                    MaterialTheme.colorScheme.secondaryContainer
                )
        )
        OutlinedTextField(
            value = fourthNumber,
            onValueChange = { },
            readOnly = true,
            enabled = false,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .size(55.dp)
                .border(
                    if (index == 4) 1.dp else 0.2.dp,
                    MaterialTheme.colorScheme.secondaryContainer
                )
        )
    }
    
    Spacer(modifier = Modifier.height(20.dp))

    Row {
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDigitClick("1") }
        ){
            Text("1", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDigitClick("2") }
        ){
            Text("2", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDigitClick("3") }
        ){
            Text("3", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }

    Row {
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDigitClick("4") }
        ){
            Text("4", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDigitClick("5") }
        ){
            Text("5", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDigitClick("6") }
        ){
            Text("6", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }

    Row {
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDigitClick("7") }
        ){
            Text("7", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDigitClick("8") }
        ){
            Text("8", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDigitClick("9") }
        ){
            Text("9", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }

    Row {
        Box(modifier=Modifier.weight(1f))
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDigitClick("0") }
        ){
            Text("0", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
        Box(
            modifier= Modifier
                .weight(1f)
                .clickable { onDelDigit() }
        ){
            Text("del", modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}