package com.example.parental_control_app.screens.parent

import com.example.parental_control_app.data.ParentScreenBottomNavRoutes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.parental_control_app.screens.SettingsScreen
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.parent.ParentNavigationViewModel

@Composable
fun ParentNavigationScreen(viewModel: ParentNavigationViewModel) {
    val controller = viewModel.getController()
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    fun setSelectedIndex(newIndex: Int) {
        selectedIndex = newIndex
    }

    ParentalControlAppTheme {
        Scaffold (
            bottomBar = { ParentNavigationBar(controller, selectedIndex, setSelectedIndex= ::setSelectedIndex)}
        ){ innerPadding ->
            Surface (
                modifier = Modifier.padding(innerPadding)
            ){
                NavHost(
                    navController = controller,
                    startDestination = ParentScreenBottomNavRoutes.HOME.toString()
                ) {
                    composable(ParentScreenBottomNavRoutes.HOME.toString()) { ParentHomeScreen(viewModel.getParentHomeViewModel()) }
                    composable(ParentScreenBottomNavRoutes.SETTINGS.toString()) { SettingsScreen(viewModel.settingsViewModelProvider) }
                }
            }
        }
    }
}


@Composable
private fun ParentNavigationBar(controller: NavHostController, selectedIndex: Int, setSelectedIndex: (newIndex: Int) -> Unit) {
    val icons = ParentNavigationViewModel.bottomNavBarIcons

    BottomAppBar {
        NavigationBar{
            icons.forEachIndexed { index, navBarIcon ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = {
                        controller.navigate(navBarIcon.route)
                        setSelectedIndex(index)
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