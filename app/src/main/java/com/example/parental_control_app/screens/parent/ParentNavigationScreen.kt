package com.example.parental_control_app.screens.parent

import ParentScreenBottomNavRoutes
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.parent.ParentHomeViewModel
import com.example.parental_control_app.viewmodels.parent.ParentNavigationViewModel

@Composable
fun ParentNavigationScreen(viewModel: ParentNavigationViewModel) {
    val controller = viewModel.getController()

    ParentalcontrolappTheme {
        Scaffold (
            bottomBar = { ParentNavigationBar(controller)}
        ){ innerPadding ->
            Surface (
                modifier = Modifier.padding(innerPadding)
            ){
                NavHost(
                    navController = controller,
                    startDestination = ParentScreenBottomNavRoutes.HOME.toString()
                ) {
                    composable(ParentScreenBottomNavRoutes.HOME.toString()) { ParentHomeScreen(viewModel.getParentHomeViewModel()) }
                    composable(ParentScreenBottomNavRoutes.SETTINGS.toString()) {
                        Button(onClick = { viewModel.onSignOut() }) {
                            Text("Sign Out")
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ParentNavigationBar(controller: NavHostController) {
    var selectedIndex by remember {
        mutableStateOf(0)
    }
    val icons = ParentNavigationViewModel.bottomNavBarIcons

    BottomAppBar {
        NavigationBar {
            icons.forEachIndexed { index, navBarIcon ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = {
                        controller.navigate(navBarIcon.route)
                        selectedIndex = index
                    },
                    icon = {
                        if (selectedIndex == index) Icon(navBarIcon.selectedIcon, navBarIcon.name)
                        else Icon(navBarIcon.unselectedIcon, navBarIcon.name)
                    } ,
                    label = { Text(navBarIcon.name) }
                )
            }
        }
    }
}