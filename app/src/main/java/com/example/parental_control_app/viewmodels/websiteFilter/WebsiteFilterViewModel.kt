package com.example.parental_control_app.viewmodels.websiteFilter

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.activities.websiteFilter.WebsiteFilterAddActivity
import com.example.parental_control_app.data.Site
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.repositories.SiteRepository
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.launch

class WebsiteFilterViewModel(
    val profileId: String,
    val activityStarterHelper: ActivityStarterHelper,
) : ViewModel(){

    private val usersRepository = UsersRepository()
    private val siteRepository = SiteRepository()

    private val _siteState = mutableStateOf<List<Site>>(listOf())
    val siteState : List<Site>
        get() = _siteState.value

    init {
        viewModelScope.launch {
            val uid = usersRepository.getProfileUID(profileId)
            _siteState.value = siteRepository.getSites(uid)
        }
    }

    fun startWebsiteFilterAddActivity() {
        activityStarterHelper.startNewActivity(
            activity = WebsiteFilterAddActivity::class.java,
            extras = mapOf(
                "kidProfileId" to profileId
            )
        )
    }

    fun deleteSite(site: Site) {
        viewModelScope.launch {
            val uid = usersRepository.getProfileUID(profileId)
            siteRepository.deleteSite(uid, site)
            _siteState.value = _siteState.value.filter { magic -> magic.url != site.url }
        }
    }
}