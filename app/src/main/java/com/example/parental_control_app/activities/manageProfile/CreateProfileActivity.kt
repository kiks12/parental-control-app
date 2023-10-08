package com.example.parental_control_app.activities.manageProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.helpers.ResultLauncherHelper
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.manageProfile.CreateProfileVIewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: CreateProfileVIewModel
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        when(activityResult.resultCode) {
            RESULT_OK -> {
                val jsonData = activityResult.data?.getStringExtra("ActivityResult").toString()
                viewModel.onMaturityLevelChange(jsonData)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resultLauncherHelper = ResultLauncherHelper(this, resultLauncher)

        viewModel = CreateProfileVIewModel(resultLauncherHelper)

        setContent {
            val state = viewModel.state
            val isParent = remember { mutableStateOf(true) }
            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.ROOT)
            val openDateDialog = remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
            var selectedDate by remember {
                mutableLongStateOf(0L)
            }

            ParentalControlAppTheme {
                Scaffold { innerPadding ->

                    Surface(Modifier.padding(innerPadding)){
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp)
                        ){

                            if (openDateDialog.value) {
                                item {
                                    val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
                                    DatePickerDialog(
                                        onDismissRequest = {
                                            if (selectedDate != datePickerState.selectedDateMillis) {
                                                openDateDialog.value = false
                                            }
                                        },
                                        confirmButton = {
                                            TextButton(
                                                onClick = {
                                                    openDateDialog.value = false
                                                    selectedDate = datePickerState.selectedDateMillis!!
                                                    viewModel.onBirthdayChange(datePickerState.selectedDateMillis!!)
                                                },
                                                enabled = confirmEnabled.value
                                            ) {
                                                Text("OK")
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(
                                                onClick = {
                                                    openDateDialog.value = false
                                                }
                                            ) {
                                                Text("Cancel")
                                            }
                                        }
                                    ) {
                                        DatePicker(state = datePickerState)
                                    }
                                }
                            }

                            item { Text("Create new Profile", fontSize = 30.sp, fontWeight = FontWeight.SemiBold) }
                            item {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 15.dp),
                                    value = state.profile.name,
                                    onValueChange = viewModel::onNameChange,
                                    label = { Text("Name") }
                                )
                            }
                            item {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 15.dp),
                                    value = if (datePickerState.selectedDateMillis != null) formatter.format(
                                        Date(datePickerState.selectedDateMillis!!)
                                    ) else "MM/DD/YYYY",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Birthday") },
                                    trailingIcon = { IconButton(onClick = { openDateDialog.value = true }) {
                                        Icon(Icons.Rounded.DateRange, "date picker")
                                    } }
                                )
                            }
                            item {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 15.dp),
                                    value = state.profile.age,
                                    onValueChange = viewModel::onAgeChange,
                                    label = { Text("Age") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                            item {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 20.dp),
                                    value = state.profile.phoneNumber!!,
                                    onValueChange = viewModel::onPhoneNumberChange,
                                    label = { Text("Phone Number") }
                                )
                            }
                            item { Text("Profile Type:") }
                            item {
                                Row(
                                    Modifier
                                        .selectableGroup()
                                        .fillMaxWidth()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isParent.value,
                                            onClick = {
                                                isParent.value = true
                                                viewModel.changeProfileType(true)
                                            },
                                            modifier = Modifier.semantics { contentDescription = "Parent Profile" }
                                        )
                                        Text("Parent")
                                    }
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        RadioButton(
                                            selected = !isParent.value,
                                            onClick = {
                                                isParent.value = false
                                                viewModel.changeProfileType(false)
                                            },
                                            modifier = Modifier.semantics { contentDescription = "Child Profile" }
                                        )
                                        Text("Child")
                                    }
                                }
                            }

                            if (isParent.value) {
                                item {
                                    Column {
                                        OutlinedTextField(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 20.dp),
                                            value = state.profile.password,
                                            onValueChange = viewModel::onPasswordChange,
                                            label = { Text("Password") },
                                        )
                                        Text(
                                            text = "Password is required to parents profile to ensure that child users will not be able to access parent specific features.",
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            item { Spacer(modifier = Modifier.height(50.dp)) }
                            item {
                                Row {
                                    FilledTonalButton(onClick = {
                                        setResult(RESULT_CANCELED)
                                        finish()
                                    }) {
                                        Text(
                                            modifier = Modifier.padding(horizontal = 10.dp),
                                            text = "Cancel"
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    if (isParent.value) {
                                        Button(
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = {
                                                val gson = Gson()
                                                val intent = Intent()
                                                intent.putExtra("ActivityResult", gson.toJson(state.profile))
                                                setResult(RESULT_OK, intent)
                                                finish()
                                            }
                                        ) {
                                            Text("Create Profile")
                                        }
                                    } else {
                                        if (state.profile.maturityLevel.isNullOrEmpty()) {
                                            Button(
                                                modifier = Modifier.fillMaxWidth(),
                                                onClick = viewModel::startSurvey
                                            ) {
                                                Text("Continue")
                                            }
                                        } else {
                                            Button(
                                                modifier = Modifier.fillMaxWidth(),
                                                onClick = {
                                                    val gson = Gson()
                                                    val intent = Intent()
                                                    intent.putExtra("ActivityResult", gson.toJson(state.profile))
                                                    setResult(RESULT_OK, intent)
                                                    finish()
                                                }
                                            ) {
                                                Text("Create Profile")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}