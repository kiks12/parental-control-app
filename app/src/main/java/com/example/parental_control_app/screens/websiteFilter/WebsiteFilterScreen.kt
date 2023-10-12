package com.example.parental_control_app.screens.websiteFilter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.websiteFilter.WebsiteFilterViewModel

@Composable
fun WebsiteFilterScreen(viewModel: WebsiteFilterViewModel, back: () -> Unit) {
    val sites = viewModel.siteState
    val profile = viewModel.profileState

    ParentalControlAppTheme {
        Scaffold(
            topBar = {
                WebsiteFilterTopBar {
                    back()
                }
            },
            floatingActionButton = {
                if (profile.parent) {
                    FloatingActionButton(
                        onClick = viewModel::startWebsiteFilterAddActivity,
                        shape = RoundedCornerShape(50),
                        content = { Icon(Icons.Rounded.Add, "Add") }
                    )
                }
            }
        ) { innerPadding ->
            LazyColumn(modifier = Modifier
                .padding(innerPadding)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Total Sites: ${sites.size}", fontSize = 20.sp)
                    }
                }

                if (sites.isEmpty()) {
                    item { Text("No sites to show") }
                } else {
                    items(sites) {site ->
                        ListItem(
                            headlineContent = { Text(site.url) },
                            trailingContent = {
                                IconButton(onClick = {
                                    if (profile.parent) {
                                        viewModel.deleteSite(site)
                                    }
                                }) {
                                   Icon(Icons.Outlined.Delete, "delete")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WebsiteFilterTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Website Filter") },
        navigationIcon = { IconButton(onClick = onBackClick) {
            Icon(Icons.Rounded.ArrowBack, "")
        } },
    )
}
