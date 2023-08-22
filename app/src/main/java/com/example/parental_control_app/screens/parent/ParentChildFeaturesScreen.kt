package com.example.parental_control_app.screens.parent

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.screens.children.FeatureIconButton
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.parent.ParentChildFeaturesViewModel

@Composable
fun ParentChildFeaturesScreen(viewModel: ParentChildFeaturesViewModel) {
    val icons = ParentChildFeaturesViewModel.featureIcons

    ParentalcontrolappTheme {
        Scaffold (
            topBar = { ParentChildFeaturesTopBar(viewModel.getOnBackClick()) }
        ){ innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding)
            ){
                Card(
                    modifier = Modifier.padding(15.dp),
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier.padding(15.dp),
                        columns = GridCells.Adaptive(minSize = 80.dp),
                    ) {
                        icons.forEachIndexed { index, icon ->
                            item {
                                FeatureIconButton(
                                    icon = icon,
                                    onClick = viewModel::onFeatureClick
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
fun ParentChildFeaturesTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        navigationIcon = { IconButton(onClick = onBackClick) {
            Icon(Icons.Rounded.ArrowBack, "")
        } },
        title = { Text("Child Profile") }
    )
}