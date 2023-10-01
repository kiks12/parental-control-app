package com.example.parental_control_app.screens.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.screens.children.FeaturesComponent
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.parent.ParentChildFeaturesViewModel
import kotlinx.coroutines.launch

@Composable
fun ParentChildFeaturesScreen(viewModel: ParentChildFeaturesViewModel) {
    val icons = ParentChildFeaturesViewModel.featureIcons
    val profile = viewModel.kidProfile
    val scope = rememberCoroutineScope()

    ParentalControlAppTheme {
        Scaffold (
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                FloatingActionButton(
                    shape = RoundedCornerShape(50),
                    onClick = {
                    if (profile.phoneLock) {
                        scope.launch {
                            viewModel.unlockChildPhone()
                        }
                    } else {
                        scope.launch {
                            viewModel.lockChildPhone()
                        }
                    }
                }) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(15.dp)
                    ){
                        if (profile.phoneLock) {
                            Icon(Icons.Filled.Lock, contentDescription = "Lock Phone", modifier = Modifier.size(25.dp))
                        } else {
                            Icon(Icons.Outlined.Lock, contentDescription = "Lock Phone", modifier = Modifier.size(25.dp))
                        }
                        Spacer(Modifier.width(5.dp))
                        if (profile.phoneLock) {
                            Text("Unlock Child Phone")
                        } else {
                            Text("Lock Child Phone")
                        }
                    }
                }
            },
            topBar = { ParentChildFeaturesTopBar(viewModel.getOnBackClick()) }
        ){ innerPadding ->
            Column (
                modifier = Modifier.padding(innerPadding)
            ){
                FeaturesComponent(profile = profile, icons = icons, onFeatureClick = viewModel::onFeatureClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentChildFeaturesTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        navigationIcon = { IconButton(onClick = onBackClick) {
            Icon(Icons.Rounded.ArrowBack, "")
        } },
        title = { Text("Child Profile") }
    )
}