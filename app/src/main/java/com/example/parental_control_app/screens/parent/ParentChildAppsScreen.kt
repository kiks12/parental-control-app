package com.example.parental_control_app.screens.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.parental_control_app.components.AppCardType
import com.example.parental_control_app.viewmodels.parent.ParentChildAppsViewModel

@Composable
fun ParentChildAppsScreen(viewModel: ParentChildAppsViewModel) {
    val loading = viewModel.loadingState
    val suggestions = viewModel.suggestionsState
    val apps = viewModel.appsState
    val icons = viewModel.iconState
    val totalScreenTime = viewModel.getTotalScreenTime()

    ParentalcontrolappTheme {
        Scaffold(
            topBar = {
                TopBar(
                    appsCount = suggestions.size + apps.size,
                    onBackClick = viewModel.onBackClick()
                )
            }
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
                        CircularProgressIndicator()
                    }
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    item { Text("Suggestions", modifier = Modifier.padding(horizontal = 10.dp)) }

                    if (suggestions.isNotEmpty()) {
                        items(suggestions) { suggestion ->
                            AppCard(
                                app = suggestion,
                                appIcon = icons[suggestion.packageName]!!,
                                totalScreenTime = totalScreenTime.toLong(),
                                type = AppCardType.SUGGESTIONS,
                                onCheckedChange = viewModel::updateAppRestriction,
                            )
                        }
                    } else {
                        item { Text("No Suggestions available", modifier = Modifier.padding(horizontal = 10.dp)) }
                    }

                    item { Text("Apps", modifier = Modifier.padding(horizontal = 10.dp)) }

                    if (apps.isNotEmpty()) {
                        items(apps) {app ->
                            AppCard(
                                app = app,
                                appIcon = icons[app.packageName]!!,
                                totalScreenTime = totalScreenTime.toLong(),
                                type = AppCardType.APP,
                                onCheckedChange = viewModel::updateAppRestriction
                            )
                        }
                    } else {
                        item { Text("No Apps available", modifier = Modifier.padding(horizontal = 10.dp)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(appsCount: Int, onBackClick: () -> Unit) {
    TopAppBar(
        navigationIcon = { IconButton(onClick = { onBackClick() }) {
            Icon(Icons.Rounded.ArrowBack, "back")
        } },
        title = { Text("Child Apps") },
        actions = {
            Text("$appsCount Apps", modifier = Modifier.padding(end = 10.dp))
        }
    )
}
