package com.example.parental_control_app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.components.AppCard
import com.example.parental_control_app.components.AppCardType
import com.example.parental_control_app.viewmodels.BlockedAppsViewModel

@Composable
fun BlockedAppsScreen(viewModel: BlockedAppsViewModel, onBackClick: () -> Unit) {
    val loading = viewModel.loadingState
    val apps = viewModel.appsState
    val icons = viewModel.iconsState

    ParentalControlAppTheme {
        Scaffold(
            topBar = {
                BlockedAppsTopBar(
                    blockedAppsCount = apps.size,
                    onBackClick = onBackClick
                )
            }
        ){ innerPadding ->
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Box (
                        modifier = Modifier
                            .height(50.dp)
                            .width(50.dp)
                    ){
                        CircularProgressIndicator()
                    }
                }
            } else if (apps.isEmpty()) {
                Box (
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ){
                    Text("No apps to show")
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    apps.forEach {
                        item {
                            icons[it.packageName]?.let { it1 ->
                                AppCard(
                                    app = it,
                                    appIcon = it1,
                                    type = AppCardType.APP,
                                    onParent = viewModel.profileState.parent,
                                    onCheckedChange = viewModel::updateAppRestriction,
                                    onTimeLimitChange = viewModel::updateAppScreenTimeLimit
                                )
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
private fun BlockedAppsTopBar(blockedAppsCount: Int, onBackClick: () -> Unit) {
    TopAppBar(
        navigationIcon = { IconButton(onClick = { onBackClick() }) {
            Icon(Icons.Rounded.ArrowBack, "")
        } },
        title = { Text("Blocked Apps") },
        actions = {
            Text("$blockedAppsCount Apps", modifier = Modifier.padding(end = 10.dp))
        }
    )
}
