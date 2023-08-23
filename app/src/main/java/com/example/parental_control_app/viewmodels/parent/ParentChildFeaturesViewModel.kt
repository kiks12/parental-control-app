package com.example.parental_control_app.viewmodels.parent

import ChildrenFeatureIcons
import FeatureIcon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.lifecycle.ViewModel
import com.example.parental_control_app.activities.parent.ParentChildAppsActivity
import com.example.parental_control_app.activities.parent.ParentChildBlockedAppsActivity
import com.example.parental_control_app.activities.parent.ParentChildLocationActivity
import com.example.parental_control_app.activities.parent.notifications.ParentChildNotificationsActivity
import com.example.parental_control_app.activities.parent.ParentChildScreenTimeActivity
import com.example.parental_control_app.activities.parent.sms.ParentChildSmsActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper

class ParentChildFeaturesViewModel : ViewModel(){
    private lateinit var profileId: String
    private lateinit var kidProfileId: String
    private lateinit var onBackClick: () -> Unit
    private lateinit var activityStarterHelper: ActivityStarterHelper

    companion object {
        val featureIcons = listOf(
            FeatureIcon("Apps", ChildrenFeatureIcons.APPS, Icons.Outlined.Menu),
            FeatureIcon("Screen Time", ChildrenFeatureIcons.SCREEN_TIME, Icons.Outlined.List),
            FeatureIcon("Blocked Apps", ChildrenFeatureIcons.BLOCKED_APPS, Icons.Outlined.Lock),
            FeatureIcon("SMS", ChildrenFeatureIcons.SMS, Icons.Outlined.Email),
            FeatureIcon("Notifications", ChildrenFeatureIcons.NOTIFICATIONS, Icons.Outlined.Notifications),
            FeatureIcon("Location", ChildrenFeatureIcons.LOCATION, Icons.Outlined.LocationOn),
        )
    }

    fun setActivityStartHelper(helper: ActivityStarterHelper) {
        activityStarterHelper = helper
    }

    fun setProfileId(id: String) {
        profileId = id
    }

    fun setKidProfileId(id: String) {
        kidProfileId = id
    }

    fun addOnBackClick(callback: () -> Unit) {
        onBackClick = callback
    }

    fun getOnBackClick() : () -> Unit{
       return onBackClick
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
        activityStarterHelper.startNewActivity(
            activity = ParentChildAppsActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startBlockedApps() {
        activityStarterHelper.startNewActivity(
            activity = ParentChildBlockedAppsActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startScreenTime() {
        activityStarterHelper.startNewActivity(
            activity = ParentChildScreenTimeActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startSMS() {
        activityStarterHelper.startNewActivity(
            activity = ParentChildSmsActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startNotifications() {
        activityStarterHelper.startNewActivity(
            activity = ParentChildNotificationsActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startLocation() {
        activityStarterHelper.startNewActivity(
            activity = ParentChildLocationActivity::class.java,
            extras = mapOf(
                "kifProfileId" to kidProfileId
            )
        )
    }
}
