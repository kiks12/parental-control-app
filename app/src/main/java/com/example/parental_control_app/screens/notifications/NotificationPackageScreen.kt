package com.example.parental_control_app.screens.notifications

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
import com.example.parental_control_app.components.NotificationPackageCard
import com.example.parental_control_app.viewmodels.notifications.NotificationPackageViewModel

@Composable
fun NotificationPackageScreen(viewModel: NotificationPackageViewModel) {
    val notifications = viewModel.notificationsState
    val packageName = viewModel.getPackageName()

    Scaffold(
        topBar = { TopBar(packageName = packageName, onBackClick = viewModel.onBackClick) }
    ){ innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ){
            if (notifications.isEmpty()) {
                Text("No Notifications to show")
            } else {
                LazyColumn {
                    notifications.forEach { notification ->
                        item { NotificationPackageCard(notification) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(packageName: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(packageName) }, 
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Rounded.ArrowBack, "Back")
            }
        }
    )
}