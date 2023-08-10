package com.example.parental_control_app.startup

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StartupScreen(viewModel: StartupViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(onClick = viewModel::signOut) {
            Text("Sign Out")
        }
    }
}
