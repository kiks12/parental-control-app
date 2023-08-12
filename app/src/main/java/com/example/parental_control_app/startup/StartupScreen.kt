package com.example.parental_control_app.startup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class UserProfileType {
    PARENT,
    CHILD,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartupScreen(viewModel: StartupViewModel) {
    val uiState = viewModel.uiState

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Surface(
            modifier = Modifier.padding(30.dp)
        ){

            if (bottomSheetState.isVisible) {
                AddProfileBottomSheet(
                    uiState = uiState,
                    viewModel = viewModel,
                    sheetState = bottomSheetState,
                    scope = scope,
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Profiles")
                ProfilesGrid(
                    uiState,
                    onAddProfile = { scope.launch { bottomSheetState.show() } },
                    viewModel,
                )

                if (uiState.user.isFirstSignIn) {
                    Button(onClick = { scope.launch { viewModel.saveProfiles() } }) {
                        Text("Save")
                    }
                } else {
                    Button(onClick = viewModel::signOut) {
                        Text("Sign Out")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfilesGrid(
    state: StartupState,
    onAddProfile: () -> Unit,
    viewModel: StartupViewModel,
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.surfaceTint,
    )

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 135.dp)){
        state.profiles.forEach {
            item {
                val index = Random.nextInt(0, colors.size)
                ProfileCard(
                    username = it.name,
                    color = colors[index],
                    onClick =  { viewModel.onProfileClick(it) }
                )
            }
        }
        if (state.user.isFirstSignIn) {
            item {
                AddProfileCard(onClick = { onAddProfile() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileBottomSheet(
    uiState: StartupState,
    viewModel: StartupViewModel,
    sheetState: SheetState,
    scope: CoroutineScope,
) {
    val dropdownExpanded = remember { mutableStateOf(false) }
    val dropdownItems = listOf(UserProfileType.PARENT.name, UserProfileType.CHILD.name)
    val dropdownSelectedIndex = remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = { scope.launch { sheetState.hide() } },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ){
            Text("Create new Profile")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.profileInput.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Name")}
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.profileInput.phoneNumber!!,
                onValueChange = viewModel::onPhoneNumberChange,
                label = { Text("Phone Number")}
            )
            Text("Type")
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopStart)) {
                Card {
                    Text(
                        text = dropdownItems[dropdownSelectedIndex.intValue],
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { dropdownExpanded.value = true })
                            .padding(10.dp)
                    )
                }
                DropdownMenu(
                    expanded = dropdownExpanded.value,
                    onDismissRequest = { dropdownExpanded.value = false },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    dropdownItems.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            text = { Text(s) },
                            onClick = {
                                dropdownSelectedIndex.intValue = index
                                dropdownExpanded.value = false
                                viewModel.onTypeChange(dropdownItems[dropdownSelectedIndex.intValue])
                            }
                        )
                    }
                }
            }

            if (dropdownItems[dropdownSelectedIndex.intValue] == UserProfileType.PARENT.name) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.profileInput.password!!,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Password") }
                )
                Text("Password is required to parents profile to ensure that child users will not be able to access parent specific features.")
            }
            
            Spacer(modifier = Modifier.height(50.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { scope.launch {viewModel.createProfile{scope.launch {sheetState.hide()}}} }
            ) {
                Text("Create Profile")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileCard(onClick: () -> Unit) {
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

@Composable
fun ProfileCard(username: String, color: Color, onClick: () -> Unit) {
    Card (
        modifier = Modifier
            .padding(10.dp)
            .aspectRatio(1f)
            .clickable { onClick() }
        ,
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ){
        Text(
            username,
            modifier = Modifier.padding(20.dp),
            textAlign = TextAlign.Center
        )
    }
}

