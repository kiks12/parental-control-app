package com.example.parental_control_app.screens.parent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.parental_control_app.screens.children.FeaturesComponent
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.parent.ParentChildFeaturesViewModel

@Composable
fun ParentChildFeaturesScreen(viewModel: ParentChildFeaturesViewModel) {
    val icons = ParentChildFeaturesViewModel.featureIcons
    val profile = viewModel.getKidProfile()

    ParentalcontrolappTheme {
        Scaffold (
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