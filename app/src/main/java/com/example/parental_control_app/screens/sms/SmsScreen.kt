package com.example.parental_control_app.screens.sms

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.parental_control_app.components.SmsCard
import com.example.parental_control_app.viewmodels.sms.SmsViewModel

@Composable
fun SmsScreen(viewModel : SmsViewModel) {
    val sms = viewModel.smsState

    Scaffold(
        topBar = { TopBar(onBackClick = viewModel.getOnBackClick()) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ){
            if (sms.isEmpty()) {
                Text("No Sms to show")
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