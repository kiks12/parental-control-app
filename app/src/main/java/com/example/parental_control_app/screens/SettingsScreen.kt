package com.example.parental_control_app.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.parental_control_app.viewmodels.children.ChildrenViewModel

@Composable
fun SettingsScreen(ignoredChildrenViewModel: ChildrenViewModel) {
    Surface {
        Button(onClick = ignoredChildrenViewModel::signOut) {
            Text("Sign Out")
        }
    }
}