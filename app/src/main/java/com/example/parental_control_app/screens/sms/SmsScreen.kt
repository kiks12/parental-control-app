package com.example.parental_control_app.screens.sms

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.components.SmsCard
import com.example.parental_control_app.viewmodels.sms.SmsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SmsScreen(viewModel : SmsViewModel, onBackClick : () -> Unit) {
    val sms = viewModel.smsState

    val permissionState = rememberPermissionState(permission = Manifest.permission.RECEIVE_SMS)

    Scaffold(
        topBar = { TopBar(onBackClick) },
        containerColor = Color.White,
        contentColor = Color.Black
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ){
            PermissionRequired(
                permissionState = permissionState,
                permissionNotGrantedContent = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text("SMS permission is required")
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = {
                            permissionState.launchPermissionRequest()
                        }) {
                            Text(
                                "Request Permission",
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                            )
                        }
                    }
                },
                permissionNotAvailableContent = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
            ) {

                if (sms.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Sms to show")
                    }
                } else {
                    LazyColumn {
                        sms.forEach {sms ->
                            item {
                                SmsCard(sms, viewModel::onSmsClick)
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
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Rounded.ArrowBack, "back")
            }
        },
        title = { Text("SMS") }
    )
}