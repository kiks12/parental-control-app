package com.example.parental_control_app.viewmodels.parent

import com.example.parental_control_app.data.FeatureIcons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.parental_control_app.activities.ActivityLogActivity
import com.example.parental_control_app.activities.ScreenTimeActivity
import com.example.parental_control_app.activities.parent.ParentChildAppsActivity
import com.example.parental_control_app.activities.BlockedAppsActivity
import com.example.parental_control_app.activities.DataUsageActivity
import com.example.parental_control_app.activities.LocationActivity
import com.example.parental_control_app.activities.websiteFilter.WebsiteFilterActivity
import com.example.parental_control_app.activities.notifications.NotificationsActivity
import com.example.parental_control_app.activities.sms.SmsActivity
import com.example.parental_control_app.data.FeatureIcon
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.coroutineScope

class ParentChildFeaturesViewModel : ViewModel(){
    private lateinit var profileId: String
    private lateinit var kidProfileId: String

    private val _kidProfile = mutableStateOf(UserProfile())
    val kidProfile : UserProfile
        get() = _kidProfile.value

    private lateinit var onBackClick: () -> Unit
    private lateinit var activityStarterHelper: ActivityStarterHelper

    private val usersRepository = UsersRepository()

    companion object {
        val featureIcons = listOf(
            FeatureIcon("Apps", FeatureIcons.APPS, Icons.Outlined.Menu),
            FeatureIcon("Blocked Apps", FeatureIcons.BLOCKED_APPS, Icons.Outlined.Lock),
            FeatureIcon("Screen Time", FeatureIcons.SCREEN_TIME, Icons.Outlined.List),
            FeatureIcon("Location", FeatureIcons.LOCATION, Icons.Outlined.LocationOn),
            FeatureIcon("Website Filter", FeatureIcons.WEBSITE_FILTER, Icons.Outlined.Edit),
            FeatureIcon("Activity Log", FeatureIcons.ACTIVITY_LOG, Icons.Outlined.Info),
            FeatureIcon("Data Usage", FeatureIcons.DATA_USAGE, Icons.Outlined.PlayArrow),
            FeatureIcon("SMS", FeatureIcons.SMS, Icons.Outlined.Call),
            FeatureIcon("Notifications", FeatureIcons.NOTIFICATIONS, Icons.Outlined.Notifications),
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
        _kidProfile.value = profile
    }

    fun addOnBackClick(callback: () -> Unit) {
        onBackClick = callback
    }

    fun getOnBackClick() : () -> Unit{
       return onBackClick
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
            FeatureIcons.DATA_USAGE -> startDataUsage()
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

    private fun startWebsiteFilter() {
        activityStarterHelper.startNewActivity(
            activity = WebsiteFilterActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startActivityLog() {
        activityStarterHelper.startNewActivity(
            ActivityLogActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    private fun startDataUsage() {
        activityStarterHelper.startNewActivity(
            DataUsageActivity::class.java,
            extras = mapOf(
                "kidProfileId" to kidProfileId
            )
        )
    }

    suspend fun lockChildPhone() {
        coroutineScope {
            val uid = usersRepository.getProfileUID(kidProfileId)
            usersRepository.lockChildPhone(uid)
            _kidProfile.value = _kidProfile.value.copy(
                phoneLock = true
            )
        }
    }

    suspend fun unlockChildPhone() {
        coroutineScope {
            val uid = usersRepository.getProfileUID(kidProfileId)
            usersRepository.unlockChildPhone(uid)
            _kidProfile.value = _kidProfile.value.copy(
                phoneLock = false
            )
        }
    }
}
