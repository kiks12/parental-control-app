package com.example.parental_control_app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.viewmodels.SettingsType
import com.example.parental_control_app.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val type = viewModel.typeProvider

    Surface {
        LazyColumn (
            modifier = Modifier.padding(30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            item {
                Text(text = "Settings", fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
            }

            if (type == SettingsType.PARENT) {
                item {
                    ListItem(
                        modifier = Modifier.clickable { },
                        headlineContent = { Text("Add Profile") },
                        leadingContent = { Icon(Icons.Rounded.Add, "") }
                    )
                }
            }

            item {
                ListItem(
                    modifier = Modifier.clickable { viewModel.signOut() },
                    headlineContent = { Text("Sign Out") },
                    leadingContent = { Icon(Icons.Rounded.ExitToApp, "") }
                )
            }
        }
    }
}