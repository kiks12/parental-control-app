package com.example.parental_control_app.screens.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.components.AppCard
import com.example.parental_control_app.viewmodels.parent.ParentChildBlockedAppsViewModel

@Composable
fun ParentChildBlockedAppsScreen(viewModel: ParentChildBlockedAppsViewModel) {
    val loading = viewModel.loadingState
    val apps = viewModel.appsState
    val icons = viewModel.iconsState
    

    ParentalcontrolappTheme {
        Scaffold(
            topBar = { TopBar(onBackClick = viewModel.onBackClick() )}
        ){ innerPadding ->
            if (loading) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .height(70.dp)
                            .width(50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Box (
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                        ){
                            CircularProgressIndicator()
                        }
                        Text("Loading")
                    }
                }
            } else if (apps.isEmpty()) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
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
                            icons[it.name]?.let { it1 ->
                                AppCard(
                                    app = it,
                                    appIcon = it1,
                                    totalScreenTime = 1,
                                    onCheckedChange = viewModel::updateAppRestriction
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
private fun TopBar(onBackClick: () -> Unit) {
    TopAppBar(
        navigationIcon = { IconButton(onClick = { onBackClick() }) {
            Icon(Icons.Rounded.ArrowBack, "")
        } },
        title = { Text("Child Blocked Apps") }
    )
}
