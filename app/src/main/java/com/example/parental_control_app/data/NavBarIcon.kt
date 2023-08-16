import androidx.compose.ui.graphics.vector.ImageVector

enum class ChildrenScreenBottomNavRoutes {
    HOME,
    SETTINGS
}

enum class ParentScreenBottomNavRoutes {
    HOME,
    SETTINGS
}

data class NavBarIcon (
    val name: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)