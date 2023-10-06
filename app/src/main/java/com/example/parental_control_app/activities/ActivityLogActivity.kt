package com.example.parental_control_app.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.ActivityLogViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityLogActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kidProfileId = intent.getStringExtra("kidProfileId")
        val activityLogViewModel = ActivityLogViewModel(kidProfileId.toString())

        setContent {
            val selectedDate = activityLogViewModel.selectedDate
            val dateToday = activityLogViewModel.dateToday
            val loading = activityLogViewModel.loadingState
            val activityLogs = activityLogViewModel.logsState
            val icons = activityLogViewModel.iconsState
            val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker, initialSelectedDateMillis = activityLogViewModel.selectedDate)
            var showDatePicker by remember {
                mutableStateOf(false)
            }

            var prevDate by remember {
                mutableLongStateOf(activityLogViewModel.selectedDate)
            }

            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.ROOT)
            val scope = rememberCoroutineScope()

            ParentalControlAppTheme {
                Scaffold(
                    topBar = {
                        TopBar { finish() }
                    }
                ){ innerPadding ->
                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { if(datePickerState.selectedDateMillis == prevDate) showDatePicker = false },
                            confirmButton = {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        showDatePicker = false
                                        scope.launch {
                                            activityLogViewModel.refreshLogs(datePickerState.selectedDateMillis!!)
                                        }
                                        prevDate = datePickerState.selectedDateMillis!!
                                    }
                                ) {
                                    Text("Confirm")
                                }
                            }) {
                            DatePicker(state = datePickerState)
                        }
                    }
                    LazyColumn(Modifier.padding(innerPadding).fillMaxSize()) {
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                            ){
                                Text("Select Date")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ){
                                    Text(formatter.format(Date(datePickerState.selectedDateMillis!!)))
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        IconButton(onClick = { showDatePicker = true }) {
                                            Icon(Icons.Outlined.DateRange, "Date")
                                        }
                                        if (dateToday != selectedDate) {
                                            FilledTonalButton(onClick = {
                                                datePickerState.selectedDateMillis = dateToday
                                                activityLogViewModel.resetDateToday()
                                            }) {
                                                Text("Reset")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (loading) {
                            item {
                                Box(
                                    Modifier
                                        .size(50.dp)
                                        .fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        } else {
                            if (activityLogs.isNotEmpty()) {
                                items(activityLogs) { activityLog ->
                                    ListItem(
                                        leadingContent = {
                                            Box(modifier = Modifier.size(50.dp)) {
                                                AsyncImage(model = icons[activityLog.packageName], contentDescription = activityLog.packageName)
                                            }
                                        },
                                        headlineContent = { Text(activityLog.label) },
                                        supportingContent = { Text(activityLog.datetime.toDate().toString()) }
                                    )
                                }
                            } else {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No Activity Logs to show")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Activity Log") },
        navigationIcon = { 
            IconButton(onClick = onBackClick) {
                Icon(Icons.Rounded.ArrowBack, "Go Back")
            }
         },
    )
}