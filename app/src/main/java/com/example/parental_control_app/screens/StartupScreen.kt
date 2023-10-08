package com.example.parental_control_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.screens.manageProfile.ProfilesGrid
import com.example.parental_control_app.viewmodels.StartupViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartupScreen(viewModel: StartupViewModel) {
    val uiState = viewModel.uiState
    val scope = rememberCoroutineScope()
    val passwordSheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (uiState.passwordBottomSheet.showSheet) {
                PasswordBottomSheet(
                    sheetState = passwordSheetState,
                    viewModel = viewModel,
                ) {
                    scope.launch {
                        passwordSheetState.hide()
                        viewModel.stopShowingParentPassword()
                    }
                }
            }
            Column(
                modifier = Modifier.padding(30.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Profiles", fontSize = 30.sp, fontWeight = FontWeight.SemiBold)

                ProfilesGrid(viewModel)

                FilledTonalButton(onClick = viewModel::signOut) {
                    Text("Sign Out")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordBottomSheet(
    sheetState: SheetState,
    viewModel: StartupViewModel,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 50.dp, top = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text("Enter your password")
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.uiState.passwordBottomSheet.value,
                onValueChange = viewModel::onBottomSheetPasswordChange,
                label = { Text("Password") },
                visualTransformation = if (viewModel.uiState.passwordBottomSheet.showPassword) VisualTransformation.None else PasswordVisualTransformation()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Checkbox(
                    viewModel.uiState.passwordBottomSheet.showPassword,
                    onCheckedChange = viewModel::onBottomSheetCheckboxChange
                )
                Text("Show Password")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::checkParentPassword
            ) {
                Text("Continue")
            }
        }
    }
}