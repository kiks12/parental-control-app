package com.example.parental_control_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.viewmodels.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    startLoginActivity: () -> Unit,
    signUpWithGoogle: () -> Unit,
) {
    val credentialState = viewModel.credentialState
    val passwordVisibility = viewModel.passwordVisibilityState

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = credentialState.value.email,
            onValueChange = viewModel::emailOnChange,
            label = { Text("Email") }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = credentialState.value.password,
            onValueChange = viewModel::passwordOnChange,
            label = { Text("Password") },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
        )
        Row {
            Checkbox(
                checked = passwordVisibility,
                onCheckedChange = viewModel::changePasswordVisibility
            )
            Text("Show Password")
        }
        ElevatedButton(
            onClick = viewModel::register,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Register",
                modifier = Modifier.padding(10.dp)
            )
        }
        ElevatedButton(
            onClick = signUpWithGoogle
        ) {
            Text(
                text = "Continue with Google",
                modifier = Modifier.padding(10.dp)
            )
        }
        ElevatedButton(
            onClick = startLoginActivity
        ) {
            Text(
                text = "Login",
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}