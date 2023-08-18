package com.example.parental_control_app.viewmodels.parent

import NavBarIcon
import ParentScreenBottomNavRoutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class ParentNavigationViewModel : ViewModel(){

    private var controller : NavHostController? = null
    private lateinit var parentHomeViewModel: ParentHomeViewModel
    lateinit var  onSignOut: () -> Unit

    companion object {
        val bottomNavBarIcons = listOf(
            NavBarIcon("Home", ParentScreenBottomNavRoutes.HOME.toString(), Icons.Filled.Home, Icons.Outlined.Home),
            NavBarIcon("Settigns", ParentScreenBottomNavRoutes.SETTINGS.toString(), Icons.Filled.Settings, Icons.Outlined.Settings)
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

    fun addOnSignOut(callback: () -> Unit) {
        onSignOut = callback
    }
}