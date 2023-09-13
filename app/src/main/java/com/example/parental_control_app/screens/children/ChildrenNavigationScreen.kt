package com.example.parental_control_app.screens.children

import ChildrenScreenBottomNavRoutes
import android.annotation.SuppressLint
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.parental_control_app.screens.SettingsScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.children.ChildrenViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChildrenScreen(viewModel: ChildrenViewModel) {
    val controller = viewModel.getController()

    ParentalcontrolappTheme {
        Scaffold(
            bottomBar = { ChildrenBottomNavigationBar(controller) } ,
            content = {
                NavHost(
                    navController = controller,
                    startDestination = "Home",
                ) {
                    composable(ChildrenScreenBottomNavRoutes.HOME.toString()) { ChildrenHomeScreen(viewModel) }
                    composable(ChildrenScreenBottomNavRoutes.SETTINGS.toString()) { SettingsScreen(viewModel) }
                }
            }
        )
    }
}

@Composable
fun ChildrenBottomNavigationBar(controller: NavHostController) {
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    val icons = ChildrenViewModel.bottomNavBarIcons

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