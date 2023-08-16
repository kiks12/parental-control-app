package com.example.parental_control_app.screens.children

import ChildrenFeatureIcons
import FeatureIcon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.viewmodels.children.ChildrenViewModel

@Composable
fun ChildrenHomeScreen(
    viewModel: ChildrenViewModel,
) {
    val icons = ChildrenViewModel.feautureIcons
    val profile = viewModel.getProfile()

    Surface {
        Column (
            modifier = Modifier.padding(10.dp)
        ){
            Text(
                text = "Welcome! ${profile?.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
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

@Composable
fun FeatureIconButton(
    icon: FeatureIcon,
    onClick: (feature: ChildrenFeatureIcons) -> Unit
) {
    Card (
        modifier = Modifier
            .aspectRatio(1f)
            .padding(5.dp)
            .clickable { onClick(icon.route) }
    ){
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Icon(
                icon.icon,
                icon.name,
                modifier = Modifier.size(35.dp)
            )
            Text(
                icon.name,
                fontSize = 10.sp,
                lineHeight = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}