package com.example.parental_control_app.screens.parent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.viewmodels.parent.ParentHomeViewModel

@Composable
fun ParentHomeScreen(viewModel: ParentHomeViewModel) {
    val profiles = viewModel.kidsProfileState
    val loading = viewModel.loadingState

    Surface (
        modifier = Modifier
            .padding(15.dp)
            .fillMaxSize()
    ){
        if (!loading) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ){
                item {
                    Text("Welcome!", fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                }
                if (profiles.isEmpty()) {
                    item {
                        Column {
                            Text("No children profiles available")
                            Button(onClick = { }) {
                                Text("Manage Profiles")
                            }
                        }
                    }
                } else {
                    items(profiles) { childProfile ->
                        ChildrenCard(
                            profile = childProfile,
                            onChildrenClick = viewModel.getOnChildrenCardClick()
                        )
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(Modifier.size(50.dp)) {
                    CircularProgressIndicator()
                }
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
                Text("${profile.name},  ${profile.age}")
                Text(profile.maturityLevel.toString())
            }
        }
    }
}