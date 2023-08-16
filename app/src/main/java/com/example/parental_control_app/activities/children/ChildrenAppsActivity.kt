package com.example.parental_control_app.activities.children

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.parental_control_app.helpers.SharedPreferencesHelper
import com.example.parental_control_app.helpers.SignOutHelper
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.children.ChildrenAppsViewModel

class ChildrenAppsActivity : AppCompatActivity() {

    private lateinit var signOutHelper: SignOutHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.PREFS_KEY, Context.MODE_PRIVATE)
        signOutHelper = SignOutHelper(this, sharedPreferences)

        val profile = SharedPreferencesHelper.getProfile(sharedPreferences)

        val viewModel = ChildrenAppsViewModel()
        if (profile == null) finish()

        val appList = viewModel.appList
        val packages = packageManager.getInstalledApplications(0)

        appList.clear()
        packages.forEach list@{
            if (packageManager.getLaunchIntentForPackage(it.packageName!!) == null) return@list
            appList.add(it)
        }

        setContent {
            ParentalcontrolappTheme {
                Scaffold (
                    topBar = { AppTopBar(onBackClick = { finish() }) },
                ){ innerPadding ->
                    LazyVerticalGrid(
                        modifier = Modifier.padding(20.dp),
                        columns = GridCells.Adaptive(70.dp),
                        contentPadding = innerPadding,
                    ) {
                        appList.forEachIndexed { index, applicationInfo ->
                            val drawable = packageManager.getApplicationIcon(applicationInfo.packageName!!)
                            item {
                                Box(
                                    modifier = Modifier.padding(5.dp)
                                ) {
                                    Column {
                                        Box(modifier = Modifier
                                            .height(50.dp)
                                            .width(50.dp)) {
                                            Image(
                                                drawable.toBitmap(
                                                    config = Bitmap.Config.ARGB_8888).asImageBitmap(),
                                                    contentDescription = "",
                                                   contentScale = ContentScale.FillBounds
                                            )
                                        }
                                        Text(applicationInfo.packageName!!, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, "Go Back")
            }
        },
        title = { Text("Apps") }
    )
}
