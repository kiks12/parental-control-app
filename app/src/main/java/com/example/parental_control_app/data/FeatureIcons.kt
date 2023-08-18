import androidx.compose.ui.graphics.vector.ImageVector

enum class ChildrenFeatureIcons {
    APPS,
    SCREEN_TIME,
    BLOCKED_APPS,
    SMS,
    NOTIFICATIONS,
    LOCATION
}

data class FeatureIcon(
    val name: String,
    val route: ChildrenFeatureIcons,
    val icon: ImageVector,
)
