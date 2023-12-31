package com.example.parental_control_app.screens.children

import com.example.parental_control_app.data.FeatureIcons
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Badge
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.data.FeatureIcon
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.viewmodels.children.ChildrenViewModel

@Composable
fun ChildrenHomeScreen(
    viewModel: ChildrenViewModel,
) {
    val icons = ChildrenViewModel.featureIcons
    val profile = viewModel.getProfile()

    Surface {
        Column (
            modifier = Modifier.padding(10.dp)
        ){
            FeaturesComponent(profile = profile!!, icons = icons, onFeatureClick = viewModel::onFeatureClick)
        }
    }
}

@Composable
fun FeatureIconButton(
    icon: FeatureIcon,
    onClick: (feature: FeatureIcons) -> Unit
) {
    Box(
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
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                icon.name,
                fontSize = 10.sp,
                lineHeight = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturesComponent(profile: UserProfile, icons: List<FeatureIcon>, onFeatureClick: (feature: FeatureIcons) -> Unit){
    ElevatedCard(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ){
        Column(
            modifier = Modifier.padding(10.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text("Welcome!", fontSize = 12.sp)
                Text("Maturity Level", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ){
                Text(
                    text = "${profile.name}, ${profile.age}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 25.sp
                )
                Text(profile.maturityLevel.toString())
            }
            Row (
                modifier = Modifier.fillMaxWidth().padding(top=5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Badge (
                        modifier = Modifier.size(5.dp),
                        containerColor = if (profile.activeStatus) Color.Green else Color.Red
                    )
                    Text(text = "Active", fontSize = 14.sp, modifier = Modifier.padding(start = 5.dp))
                }
                Text(
                    text = profile.deviceName.ifEmpty { "No Device" },
                    fontSize = 14.sp
                )
            }
        }
    }
    LazyVerticalGrid(
        modifier = Modifier.padding(10.dp),
        columns = GridCells.Adaptive(minSize = 85.dp),
    ) {
        icons.forEach { icon ->
            item {
                FeatureIconButton(
                    icon = icon,
                    onClick = onFeatureClick
                )
            }
        }
    }
}