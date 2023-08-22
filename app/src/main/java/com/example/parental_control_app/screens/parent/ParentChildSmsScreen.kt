package com.example.parental_control_app.screens.parent

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.parental_control_app.viewmodels.parent.ParentChildSmsViewModel

@Composable
fun ParentChildSmsScreen(viewModel : ParentChildSmsViewModel) {
    Surface {
        Text("Paren Child SMS Screen")
    }
}