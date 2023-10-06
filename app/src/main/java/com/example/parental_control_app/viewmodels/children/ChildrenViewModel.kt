package com.example.parental_control_app.viewmodels.children

import com.example.parental_control_app.data.FeatureIcons
import com.example.parental_control_app.data.ChildrenScreenBottomNavRoutes
import com.example.parental_control_app.data.NavBarIcon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
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
import com.example.parental_control_app.activities.ActivityLogActivity
import com.example.parental_control_app.activities.LocationActivity
import com.example.parental_control_app.activities.ScreenTimeActivity
import com.example.parental_control_app.activities.children.ChildrenAppsActivity
import com.example.parental_control_app.activities.notifications.NotificationsActivity
import com.example.parental_control_app.activities.BlockedAppsActivity
import com.example.parental_control_app.activities.sms.SmsActivity
import com.example.parental_control_app.activities.websiteFilter.WebsiteFilterActivity
import com.example.parental_control_app.data.FeatureIcon
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.viewmodels.SettingsViewModel

class ChildrenViewModel(
    settingsViewModel: SettingsViewModel,
    private val activityStarterHelper: ActivityStarterHelper,
) : ViewModel(){

    companion object {
        val featureIcons = listOf(
            FeatureIcon("Apps", FeatureIcons.APPS, Icons.Outlined.Menu),
            FeatureIcon("Blocked Apps", FeatureIcons.BLOCKED_APPS, Icons.Outlined.Lock),
            FeatureIcon("Screen Time", FeatureIcons.SCREEN_TIME, Icons.Outlined.List),
            FeatureIcon("Location", FeatureIcons.LOCATION, Icons.Outlined.LocationOn),
            FeatureIcon("Website Filter", FeatureIcons.WEBSITE_FILTER, Icons.Outlined.Edit),
            FeatureIcon("Activity Log", FeatureIcons.ACTIVITY_LOG, Icons.Outlined.Info),
            FeatureIcon("SMS", FeatureIcons.SMS, Icons.Outlined.Call),
            FeatureIcon("Notifications", FeatureIcons.NOTIFICATIONS, Icons.Outlined.Notifications),
        )

        val bottomNavBarIcons = listOf(
            NavBarIcon("Home", ChildrenScreenBottomNavRoutes.HOME.toString(), Icons.Filled.Home, Icons.Outlined.Home),
            NavBarIcon("Settings", ChildrenScreenBottomNavRoutes.SETTINGS.toString(), Icons.Filled.Settings, Icons.Outlined.Settings),
        )
    }

    private var controller : NavHostController? = null
    private var profile : UserProfile? = null
    val settingsViewModelProvider = settingsViewModel

    @Composable
    fun getController(): NavHostController {
        if (controller == null) controller = rememberNavController()
        return controller!!
    }

    fun setProfile(prof: UserProfile) {
        profile = prof
    }

    fun getProfile() : UserProfile? {
        return profile
    }

    fun onFeatureClick(feature: FeatureIcons) {
        when(feature) {
            FeatureIcons.APPS -> startApps()
            FeatureIcons.BLOCKED_APPS -> startBlockedApps()
            FeatureIcons.SCREEN_TIME -> startScreenTime()
            FeatureIcons.LOCATION -> startLocation()
            FeatureIcons.WEBSITE_FILTER -> startWebsiteFilter()
            FeatureIcons.ACTIVITY_LOG -> startActivityLog()
            FeatureIcons.SMS -> startSMS()
            FeatureIcons.NOTIFICATIONS -> startNotifications()
        }
    }

    private fun startApps() {
        activityStarterHelper.startNewActivity(ChildrenAppsActivity::class.java)
    }

    private fun startBlockedApps() {
        activityStarterHelper.startNewActivity(
            BlockedAppsActivity::class.java,
            extras = mapOf(
                "kidProfileId" to profile?.profileId!!,
            )
        )
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

    private fun startWebsiteFilter() {
        activityStarterHelper.startNewActivity(
            WebsiteFilterActivity::class.java,
            extras = mapOf(
                "kidProfileId" to profile?.profileId!!
            )
        )
    }

    private fun startActivityLog() {
        activityStarterHelper.startNewActivity(
            ActivityLogActivity::class.java,
            extras = mapOf(
                "kidProfileId" to profile?.profileId!!
            )
        )
    }
}