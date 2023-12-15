package com.example.parental_control_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.components.DonutChart
import com.example.parental_control_app.data.Response
import com.example.parental_control_app.viewmodels.ScreenTimeViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun ScreenTimeScreen(viewModel: ScreenTimeViewModel, onBackClick: () -> Unit){
    val profile = viewModel.profileState
    val isLoading = viewModel.loadingState
    val data = viewModel.donutChartData
    var selectedTabRow by remember { mutableIntStateOf(0) }

    val snackBarHostState = remember { SnackbarHostState() }

    // SCREEN TIME LIMIT STATE
    val screenTimeLimitState = viewModel.screenTimeLimitState

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { TopBar(onBackClick = onBackClick) },
        floatingActionButton = {
            if(profile.parent) {
                FloatingActionButton(
                    onClick = { selectedTabRow = 2 },
                    shape = RoundedCornerShape(50)
                ) {
                   Text("Set Lock Time", modifier = Modifier.padding(15.dp))
                }
            }
        }
    ){ innerPadding ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                CircularProgressIndicator()
                Text("Loading")
            }
        } else {
            Column (
                modifier = Modifier.padding(innerPadding)
            ){
                TabRow(selectedTabIndex = selectedTabRow) {
                    if (profile.parent) {
                        Tab(
                            selected = selectedTabRow == 0,
                            onClick = { selectedTabRow = 0 },
                            text = { Text("Apps Screen Time") }
                        )
                        Tab(
                            selected = selectedTabRow == 1,
                            onClick = { selectedTabRow = 1},
                            text = { Text("Set Lock Time") }
                        )
                    } else {
                        Tab(
                            selected = selectedTabRow == 0,
                            onClick = { selectedTabRow = 0 },
                            text = { Text("Apps Screen Time") }
                        )
                        Tab(
                            selected = selectedTabRow == 1,
                            onClick = { selectedTabRow = 1},
                            text = { Text("Lock Time") }
                        )
                    }
                }

                LazyColumn{
                    if (selectedTabRow == 0) {
                        item {
                            Box(modifier = Modifier.padding(20.dp)) {
                                DonutChart(
                                    data = data
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(20.dp)) }
                        items(data.items) { eachData ->
                            ListItem(
                                headlineContent = { Text(eachData.title) },
                                supportingContent = {
                                    Column {
                                        Text(String.format("%dh %dm %ds",
                                            TimeUnit.MILLISECONDS.toHours(eachData.amount.toLong()),
                                            TimeUnit.MILLISECONDS.toMinutes(eachData.amount.toLong()) - TimeUnit.HOURS.toMinutes(
                                                TimeUnit.MILLISECONDS.toHours(eachData.amount.toLong())),
                                            TimeUnit.MILLISECONDS.toSeconds(eachData.amount.toLong()) - TimeUnit.MINUTES.toSeconds(
                                                TimeUnit.MILLISECONDS.toMinutes(eachData.amount.toLong()))
                                        ))
                                        LinearProgressIndicator(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(5.dp),
                                            color = eachData.color,
                                            progress = eachData.amount / data.totalAmount
                                        )
                                    }
                                }
                            )
                        }
                    }

                    if (profile.parent && selectedTabRow == 1) {
                        item {
                            SetLimitScreen(
                                screenTimeLimit = screenTimeLimitState,
                                snackBarHostState = snackBarHostState,
                                setScreenTimeState = viewModel::setScreenTimeState,
                                removeChildScreenTimeLimit = viewModel::removeScreenTimeLimit,
                                setChildScreenTimeLimit = viewModel::setChildScreenTimeLimit
                            )
                        }
                    }

                    if (profile.child && selectedTabRow == 1) {
                        item {
                            SetLimitScreen(
                                screenTimeLimit = screenTimeLimitState,
                                snackBarHostState = snackBarHostState,
                                setScreenTimeState = viewModel::setScreenTimeState,
                                removeChildScreenTimeLimit = viewModel::removeScreenTimeLimit,
                                setChildScreenTimeLimit = viewModel::setChildScreenTimeLimit,
                                readOnly = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetLimitScreen(
    screenTimeLimit: Map<String, String>,
    snackBarHostState: SnackbarHostState,
    setScreenTimeState: (time: String, type: String) -> Unit,
    removeChildScreenTimeLimit: suspend () -> Response?,
    setChildScreenTimeLimit: suspend (limit: Long) -> Response?,
    readOnly: Boolean = false,
) {
    val scope = rememberCoroutineScope()

    Column (
        modifier = Modifier
            .padding(15.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Column(
            modifier = Modifier.fillMaxWidth()
        ){
            if (readOnly) {
                Text("Lock Time")
            } else {
                Text("Lock Time")
                Text("Set your child's lock time")
            }
        }

        Spacer(Modifier.height(10.dp))
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 20.sp),
                readOnly = readOnly,
                value = screenTimeLimit.getValue("HOURS").toString(),
                onValueChange = { setScreenTimeState(it, "HOURS") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = "Hours") }
            )
            Spacer(Modifier.width(10.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 20.sp),
                readOnly = readOnly,
                value = screenTimeLimit.getValue("MINUTES").toString(),
                onValueChange = { setScreenTimeState(it, "MINUTES") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = "Minutes") }
            )
            Spacer(Modifier.width(10.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 20.sp),
                readOnly = readOnly,
                value = screenTimeLimit.getValue("SECONDS").toString(),
                onValueChange = { setScreenTimeState(it, "SECONDS") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = "Seconds") }
            )
        }
        Spacer(Modifier.height(10.dp))

        Button(
            enabled = !readOnly,
            onClick = {
                scope.launch {
                    val hours = screenTimeLimit.getValue("HOURS")
                    val minutes = screenTimeLimit.getValue("MINUTES")
                    val seconds = screenTimeLimit.getValue("SECONDS")

                    if (
                        hours.isNotEmpty() && hours.isNotBlank() && minutes.isNotEmpty()
                        && minutes.isNotBlank() && seconds.isNotBlank() && seconds.isNotEmpty()
                        ) {
                            val hoursMillis = TimeUnit.HOURS.toMillis(hours.toLong())
                            val minutesMillis = TimeUnit.MINUTES.toMillis(minutes.toLong())
                            val secondsMillis = TimeUnit.SECONDS.toMillis(seconds.toLong())
                            val response = setChildScreenTimeLimit(hoursMillis + minutesMillis + secondsMillis)
                                ?: return@launch

                            snackBarHostState.showSnackbar(response.message)
                    } else {
                        snackBarHostState.showSnackbar("Please fill up all fields.")
                    }
                }
            }
        ) {
            Text("Set", modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp))
        }

        FilledTonalButton(
            enabled = !readOnly,
            onClick = {
                scope.launch {
                    val response = removeChildScreenTimeLimit() ?: return@launch
                    snackBarHostState.showSnackbar(response.message)
                }
            }
        ) {
            Text("Remove Limit", modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Lock Time") },
        navigationIcon = { IconButton(onClick = onBackClick) {
            Icon(Icons.Rounded.ArrowBack, "back")
        } }
    )
}