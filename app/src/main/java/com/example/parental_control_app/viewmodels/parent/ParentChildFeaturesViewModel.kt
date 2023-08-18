package com.example.parental_control_app.viewmodels.parent

import ChildrenFeatureIcons
import FeatureIcon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.lifecycle.ViewModel
import com.example.parental_control_app.activities.parent.ParentChildAppsActivity
import com.example.parental_control_app.activities.parent.ParentChildBlockedAppsActivity
import com.example.parental_control_app.helpers.ActivityStarterHelper

class ParentChildFeaturesViewModel : ViewModel(){
    private lateinit var profileId: String
    private lateinit var kidProfileId: String
    private lateinit var onBackClick: () -> Unit
    private lateinit var activityStarterHelper: ActivityStarterHelper

    companion object {
        val feautureIcons = listOf(
            FeatureIcon("Apps", ChildrenFeatureIcons.APPS, Icons.Outlined.Menu),
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
            ChildrenFeatureIcons.BLOCKED_APPS -> startBlockedApps()
            ChildrenFeatureIcons.SMS -> startSMS()
            ChildrenFeatureIcons.NOTIFICATIONS -> startNotifications()
            ChildrenFeatureIcons.LOCATION -> startLocation()
            else -> {}
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
    private fun startSMS() {
        TODO("Parent Child Features - SMS Activity")
    }
    private fun startNotifications() {
        TODO("Parent Child Features - Notifications Activity")
    }
    private fun startLocation() {
        TODO("Parent Child Features - Location Activity")
    }
}
