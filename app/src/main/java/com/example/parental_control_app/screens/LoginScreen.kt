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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parental_control_app.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    signInWithGoogle: () -> Unit,
    startRegistrationActivity: () -> Unit,
) {
    val credentialState = viewModel.credentialState
    val passwordVisibility = viewModel.passwordVisibilityState

    Scaffold { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(30.dp)
            ){
                Column {
                    Text("KidsGuard", fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                    Text("Please login to continue")
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = credentialState.value.email,
                    onValueChange = viewModel::emailOnChange,
                    label = { Text("Email") }
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = credentialState.value.password,
                    onValueChange = viewModel::passwordOnChange,
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Checkbox(checked = passwordVisibility, onCheckedChange = viewModel::onCheckBoxChange)
                    Text("Show Password")
                }
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = viewModel::login,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Login",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                FilledTonalButton(
                    onClick = signInWithGoogle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Login with Google",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(30.dp)
            ){
                Text("Don't have an account?", fontSize = 12.sp)
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = startRegistrationActivity
                ) {
                    Text(text = "Create an Account")
                }
            }
        }
    }

}
