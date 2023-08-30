package com.example.parental_control_app.screens.parent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.parental_control_app.viewmodels.parent.ParentChildLocationViewModel

@Composable
fun ParentChildLocationScreen(viewModel: ParentChildLocationViewModel) {

    Scaffold {innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            Text("sdasdff")
        }
    }
}