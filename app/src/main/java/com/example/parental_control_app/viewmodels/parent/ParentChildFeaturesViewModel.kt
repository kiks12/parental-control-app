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
import com.example.parental_control_app.activities.ScreenTimeActivity
import com.example.parental_control_app.activities.parent.ParentChildAppsActivity
import com.example.parental_control_app.activities.BlockedAppsActivity
import com.example.parental_control_app.activities.LocationActivity
import com.example.parental_control_app.activities.notifications.NotificationsActivity
import com.example.parental_control_app.activities.sms.SmsActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.repositories.users.UserProfile

class ParentChildFeaturesViewModel : ViewModel(){
    private lateinit var profileId: String
    private lateinit var kidProfileId: String
    private lateinit var kidProfile : UserProfile
    private lateinit var onBackClick: () -> Unit
    private lateinit var activityStarterHelper: ActivityStarterHelper

    companion object {
        val featureIcons = listOf(
            FeatureIcon("Apps", ChildrenFeatureIcons.APPS, Icons.Outlined.Menu),
            FeatureIcon("Blocked Apps", ChildrenFeatureIcons.BLOCKED_APPS, Icons.Outlined.Lock),
            FeatureIcon("Screen Time", ChildrenFeatureIcons.SCREEN_TIME, Icons.Outlined.List),
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

    fun setKidProfile(profile: UserProfile) {
        kidProfile = profile
    }

    fun getKidProfile() : UserProfile{
        return kidProfile
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
            activity = BlockedAppsActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startScreenTime() {
        activityStarterHelper.startNewActivity(
            activity = ScreenTimeActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startSMS() {
        activityStarterHelper.startNewActivity(
            activity = SmsActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startNotifications() {
        activityStarterHelper.startNewActivity(
            activity = NotificationsActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startLocation() {
        activityStarterHelper.startNewActivity(
            activity = LocationActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }
}
