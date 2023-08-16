package com.example.parental_control_app.viewmodels.children

import android.content.pm.ApplicationInfo
import androidx.lifecycle.ViewModel

class ChildrenAppsViewModel : ViewModel(){

    val appList = mutableListOf<ApplicationInfo>()

}