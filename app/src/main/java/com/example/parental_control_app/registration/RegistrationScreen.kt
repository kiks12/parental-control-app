package com.example.parental_control_app.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    startLoginActivity: () -> Unit,
    startGoogleSignUpActivity: () -> Unit,
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
        ElevatedButton(
            onClick = { viewModel.register() },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Register",
                modifier = Modifier.padding(10.dp)
            )
        }
        ElevatedButton(
            onClick = { startGoogleSignUpActivity() }
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


@Preview
@Composable
fun RegistrationScreenPreview() {
    val registrationViewModel = RegistrationViewModel()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
    ) {
        RegistrationScreen(registrationViewModel, {}, {})
    }
}