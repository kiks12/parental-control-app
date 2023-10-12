package com.example.parental_control_app.screens.manageProfile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.viewmodels.StartupViewModel
import kotlin.random.Random

data class ProfileColor(
    val containerColor: Color,
    val contentColor: Color,
)

@Composable
fun ProfilesGrid(
    viewModel: StartupViewModel,
) {
    val profiles = viewModel.profilesState
    val showAddButton = viewModel.uiState.user.firstSignIn
    val showSaveButton = viewModel.uiState.user.firstSignIn
    val showDeleteButton = viewModel.uiState.user.firstSignIn

    val colors = listOf(
        ProfileColor(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
        ProfileColor(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
        ProfileColor(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer),
        ProfileColor(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
        ProfileColor(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 125.dp)){
            items(profiles){profile ->
                val index = remember { Random.nextInt(0, colors.size) }
                ProfileCard(
                    profile = profile,
                    colors = colors[index],
                    showDeleteButton = showDeleteButton,
                    onClick = {
                        if(!viewModel.uiState.user.firstSignIn) {
                            viewModel.setClickedProfile(profile)
                            if (profile.child) viewModel.startChildActivity()
                            else viewModel.getParentPassword(profile.password)
                        }
                    },
                    onDelete = {
                        viewModel.deleteProfile(profile.name)
                    }
                )
            }

            if (showAddButton) {
                item {
                    AddProfileButton(onClick = viewModel::startCreatingProfile)
                }
            }
        }
        if (showSaveButton) {
            Button(onClick = viewModel::saveProfiles) {
                Text("Save")
            }
        }
    }
}

@Composable
fun ProfileCard(
    profile: UserProfile,
    colors: ProfileColor,
    showDeleteButton: Boolean,
    onClick: () -> Unit, onDelete: () -> Unit
) {
    Card (
        modifier = Modifier
            .padding(12.dp)
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = colors.containerColor,
            contentColor = colors.contentColor
        )
    ){
        Row(
            Modifier
                .padding(start = 15.dp, top = 15.dp, bottom = 15.dp, end = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column {
                if(profile.parent)
                    Text("Parent", fontSize = 12.sp)
                else
                    Text("Child", fontSize = 12.sp)
                Text(
                    profile.name,
                    fontSize = 17.sp
                )
            }
            if(showDeleteButton) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, "Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileButton(onClick: () -> Unit) {
    Card (
        modifier = Modifier
            .padding(10.dp)
            .aspectRatio(1f),
        onClick = onClick
    ){
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Icon(Icons.Rounded.Add, "")
            Text("Add Profile")
        }
    }
}
