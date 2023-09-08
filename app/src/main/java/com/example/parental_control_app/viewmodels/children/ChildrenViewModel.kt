package com.example.parental_control_app.viewmodels.children

import ChildrenFeatureIcons
import ChildrenScreenBottomNavRoutes
import FeatureIcon
import NavBarIcon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.example.parental_control_app.activities.LocationActivity
import com.example.parental_control_app.activities.ScreenTimeActivity
import com.example.parental_control_app.activities.children.ChildrenAppsActivity
import com.example.parental_control_app.activities.children.ChildrenBlockedAppsActivity
import com.example.parental_control_app.activities.notifications.NotificationsActivity
import com.example.parental_control_app.activities.sms.SmsActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.repositories.users.UserProfile

class ChildrenViewModel(
    private val activityStarterHelper: ActivityStarterHelper,
) : ViewModel(){

    companion object {
        val featureIcons = listOf(
            FeatureIcon("Apps", ChildrenFeatureIcons.APPS, Icons.Outlined.Menu),
            FeatureIcon("Screen Time", ChildrenFeatureIcons.SCREEN_TIME, Icons.Outlined.List),
            FeatureIcon("Blocked Apps", ChildrenFeatureIcons.BLOCKED_APPS, Icons.Outlined.Lock),
            FeatureIcon("SMS", ChildrenFeatureIcons.SMS, Icons.Outlined.Email),
            FeatureIcon("Notifications", ChildrenFeatureIcons.NOTIFICATIONS, Icons.Outlined.Notifications),
            FeatureIcon("Location", ChildrenFeatureIcons.LOCATION, Icons.Outlined.LocationOn),
        )

        val bottomNavBarIcons = listOf(
            NavBarIcon("Home", ChildrenScreenBottomNavRoutes.HOME.toString(), Icons.Filled.Home, Icons.Outlined.Home),
            NavBarIcon("Settings", ChildrenScreenBottomNavRoutes.SETTINGS.toString(), Icons.Filled.Settings, Icons.Outlined.Settings),
        )
    }

    private var controller : NavHostController? = null
    private lateinit var signOutFunction : () -> Unit
    private var profile : UserProfile? = null

    @Composable
    fun getController(): NavHostController {
        if (controller == null) controller = rememberNavController()
        return controller!!
    }

    fun setSignOutFunction(callback: () -> Unit) {
        signOutFunction = callback
    }

    fun signOut() {
        signOutFunction()
    }

    fun setProfile(prof: UserProfile) {
        profile = prof
    }

    fun getProfile() : UserProfile? {
        return profile
    }

    fun onFeatureClick(feature: ChildrenFeatureIcons) {
        when(feature) {
            ChildrenFeatureIcons.APPS -> startApps()
            ChildrenFeatureIcons.SCREEN_TIME -> startScreenTime()
            ChildrenFeatureIcons.BLOCKED_APPS -> startBlockedApps()
            ChildrenFeatureIcons.SMS -> startSMS()
            ChildrenFeatureIcons.NOTIFICATIONS -> startNotifications()
            ChildrenFeatureIcons.LOCATION -> startLocation()
        }
    }

    private fun startApps() {
        activityStarterHelper.startNewActivity(ChildrenAppsActivity::class.java)
    }

    private fun startBlockedApps() {
        activityStarterHelper.startNewActivity(ChildrenBlockedAppsActivity::class.java)
    }

    private fun startScreenTime() {
        activityStarterHelper.startNewActivity(
            ScreenTimeActivity::class.java,
            extras = mapOf(
                "kidProfileId" to profile?.profileId!!,
            )
        )
    }

    private fun startSMS() {
        activityStarterHelper.startNewActivity(
            SmsActivity::class.java,
            extras = mapOf(
                "kidProfileId" to profile?.profileId!!,
            )
        )
    }

    private fun startNotifications() {
        activityStarterHelper.startNewActivity(
            NotificationsActivity::class.java,
            extras = mapOf(
                "kidProfileId" to profile?.profileId!!,
            )
        )
    }

    private fun startLocation() {
        activityStarterHelper.startNewActivity(
            LocationActivity::class.java,
            extras = mapOf(
                "kidProfileId" to profile?.profileId!!,
            )
        )
    }
}