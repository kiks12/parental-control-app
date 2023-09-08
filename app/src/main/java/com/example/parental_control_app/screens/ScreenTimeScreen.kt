package com.example.parental_control_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.components.DonutChart
import com.example.parental_control_app.viewmodels.ScreenTimeViewModel

@Composable
fun ScreenTimeScreen(viewModel: ScreenTimeViewModel){
    val isLoading = viewModel.loadingState
    val data = viewModel.donutChartData

    Scaffold(
        topBar = { TopBar(onBackClick = viewModel.onBackClick) }
    ){ innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ){
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    CircularProgressIndicator()
                    Text("Loading")
                }
            } else {
                LazyColumn{
                    item {
                        Box(modifier = Modifier.padding(20.dp)) {
                            DonutChart(
                                data = data
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                    items(data.items) { eachData ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ){
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ){
                                Text(eachData.title, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(5.dp))
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp),
                                    color = eachData.color,
                                    progress = eachData.amount / data.totalAmount
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
        title = { Text("Screen Time") },
        navigationIcon = { IconButton(onClick = onBackClick) {
            Icon(Icons.Rounded.ArrowBack, "back")
        } }
    )
}