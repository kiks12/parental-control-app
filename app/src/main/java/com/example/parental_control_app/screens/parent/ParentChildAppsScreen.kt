package com.example.parental_control_app.screens.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.components.AppCard
import com.example.parental_control_app.components.AppCardType
import com.example.parental_control_app.viewmodels.parent.ParentChildAppsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentChildAppsScreen(viewModel: ParentChildAppsViewModel, onBackClick: () -> Unit) {
    val loading = viewModel.loadingState
    val recommendations = viewModel.recommendationState
    val apps = viewModel.appsState
    val icons = viewModel.iconState

    val selectedTabIndex = remember { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    ParentalControlAppTheme {
        Scaffold(
            topBar = {
                TopBar(
                    appsCount = recommendations.size + apps.size,
                    onBackClick = onBackClick
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
                Column(
                    Modifier
                        .padding(innerPadding)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ){
                    TabRow(selectedTabIndex = selectedTabIndex.intValue){
                        Tab(
                            selected = selectedTabIndex.intValue == 0,
                            onClick = { selectedTabIndex.intValue = 0 },
                            text = { Text("Apps") }
                        )
                        Tab(
                            selected = selectedTabIndex.intValue == 1,
                            onClick = { selectedTabIndex.intValue = 1 },
                            text = { Text("Recommended") }
                        )
                    }
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        DockedSearchBar(
                            modifier = Modifier.padding(10.dp).fillMaxWidth(),
                            query = query,
                            onQueryChange = { query = it } ,
                            onSearch = {
                                active = false
                                viewModel.search(query)
                            },
                            active = active,
                            onActiveChange = { active = it },
                            placeholder = { Text("Search applications") },
                            leadingIcon = { Icon(Icons.Rounded.Search, "") },
                            trailingIcon = {
                                IconButton(onClick = viewModel::refresh, modifier = Modifier.padding(10.dp)) {
                                    Icon(Icons.Rounded.Refresh, "")
                                }
                            }
                        ) {
                            LazyColumn {
                                items(recommendations) {recommendation ->
                                    ListItem(headlineContent = { recommendation.label })
                                }
                            }
                        }
                    }
                    LazyColumn(Modifier.fillMaxSize()) {
                        if (recommendations.isNotEmpty()) {
                            items(recommendations) { suggestion ->
                                AppCard(
                                    app = suggestion,
                                    appIcon = icons[suggestion.packageName]!!,
                                    type = AppCardType.SUGGESTIONS,
                                    onParent = viewModel.profileState.parent,
                                    onCheckedChange = viewModel::updateAppRestriction,
                                    onTimeLimitChange = viewModel::updateAppScreenTimeLimit
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = "No Recommended Apps to block",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        if (selectedTabIndex.intValue == 0) {
                            if (apps.isNotEmpty()) {
                                items(apps) {app ->
                                    AppCard(
                                        app = app,
                                        appIcon = icons[app.packageName]!!,
                                        type = AppCardType.APP,
                                        onParent = viewModel.profileState.parent,
                                        onCheckedChange = viewModel::updateAppRestriction,
                                        onTimeLimitChange = viewModel::updateAppScreenTimeLimit
                                    )
                                }
                            } else {
                                item {
                                    Text(
                                        text = "No Apps available",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
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
