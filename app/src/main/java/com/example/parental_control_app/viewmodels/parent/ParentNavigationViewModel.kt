package com.example.parental_control_app.viewmodels.parent

import com.example.parental_control_app.data.NavBarIcon
import com.example.parental_control_app.data.ParentScreenBottomNavRoutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.parental_control_app.viewmodels.SettingsViewModel

class ParentNavigationViewModel(
    settingsViewModel: SettingsViewModel
) : ViewModel(){

    private var controller : NavHostController? = null
    private lateinit var parentHomeViewModel: ParentHomeViewModel
    val settingsViewModelProvider = settingsViewModel

    companion object {
        val bottomNavBarIcons = listOf(
            NavBarIcon("Home", ParentScreenBottomNavRoutes.HOME.toString(), Icons.Filled.Home, Icons.Outlined.Home),
            NavBarIcon("Settings", ParentScreenBottomNavRoutes.SETTINGS.toString(), Icons.Filled.Settings, Icons.Outlined.Settings)
        )
    }

    @Composable
    fun getController() : NavHostController{
        if (controller == null) controller = rememberNavController()
        return controller!!
    }

    fun setParentHomeViewModel(viewModel: ParentHomeViewModel) {
        parentHomeViewModel = viewModel
    }

    fun getParentHomeViewModel(): ParentHomeViewModel {
        return parentHomeViewModel
    }

}