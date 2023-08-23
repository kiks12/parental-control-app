package com.example.parental_control_app.screens.parent.sms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.viewmodels.parent.sms.ParentChildSmsViewModel

@Composable
fun ParentChildSmsScreen(viewModel : ParentChildSmsViewModel) {
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

@Composable
fun SmsCard(sender: String, onSmsClick: (documentId: String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(10.dp).clickable { onSmsClick(sender) }
    ){
        Text(sender)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBackClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Rounded.ArrowBack, "")
            }
        },
        title = { Text("Child SMS") }
    )
}