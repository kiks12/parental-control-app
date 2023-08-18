package com.example.parental_control_app.screens.parent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.viewmodels.parent.ParentHomeViewModel

@Composable
fun ParentHomeScreen(viewModel: ParentHomeViewModel) {
    val profiles = viewModel.kidsProfile

    Surface (
        modifier = Modifier.padding(10.dp)
    ){
        Column (
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Welcome! ")
            profiles.forEach { profile ->
                ChildrenCard(
                    profile = profile,
                    onChildrenClick = viewModel.getOnChildrenCardClick()
                )
            }
        }
    }
}

@Composable
private fun ChildrenCard(
    profile: UserProfile,
    onChildrenClick: (profileId: String) -> Unit
) {
    ElevatedCard (
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable { onChildrenClick(profile.profileId) }
    ){
        Column(
            modifier = Modifier.padding(10.dp)
        ){
            if (profile.parent) Text("Parent", fontSize = 10.sp)
            if (profile.child) Text("Child", fontSize = 10.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(profile.name)
                Text(profile.maturityLevel.toString())
            }
        }
    }
}