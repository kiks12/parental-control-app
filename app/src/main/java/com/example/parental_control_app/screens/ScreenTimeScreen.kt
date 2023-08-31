package com.example.parental_control_app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.parental_control_app.viewmodels.ScreenTimeViewModel

@Composable
fun ScreenTimeScreen(viewModel: ScreenTimeViewModel){
    val apps = viewModel.appState
    val icons = viewModel.iconState
    val totalScreenTime = viewModel.totalScreenTimeState
    val isLoading = viewModel.loadingState

    Scaffold(
        topBar = { TopBar(onBackClick = viewModel.onBackClick) }
    ){ innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ){
            if (isLoading) {
                Column {
                    CircularProgressIndicator()
                    Text("Loading")
                }
            } else {
                Text("$totalScreenTime")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Screen Time") },
        navigationIcon = { IconButton(onClick = onBackClick) {
            Icon(Icons.Rounded.ArrowBack, "back")
        } }
    )

}