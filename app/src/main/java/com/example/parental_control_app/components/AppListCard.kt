package com.example.parental_control_app.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parental_control_app.data.UserApps
import java.util.concurrent.TimeUnit

enum class AppCardType {
    SUGGESTIONS,
    APP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCard(
    app: UserApps,
    appIcon: String,
    type: AppCardType,
    onCheckedChange: (appName: String, newRestriction: Boolean) -> Unit,
) {
    val restricted = remember { mutableStateOf(app.restricted) }

    ListItem(
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(app.label)
                if (type == AppCardType.SUGGESTIONS) {
                    Badge(Modifier.padding(start = 5.dp))
                }
            }
        },
        supportingContent = {
            if (app.screenTime.toInt() == 0) {
                Text("00:00:00")
            } else {
                Text(String.format("%d:%d:%d",
                    TimeUnit.MILLISECONDS.toHours(app.screenTime),
                    TimeUnit.MILLISECONDS.toMinutes(app.screenTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(app.screenTime)),
                    TimeUnit.MILLISECONDS.toSeconds(app.screenTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(app.screenTime))
                ))
            }
        },
        leadingContent = {
            Box(modifier = Modifier.size(50.dp)) {
                AsyncImage(model = appIcon, contentDescription = app.packageName)
            }
        },
        trailingContent = {
            Switch(
                checked = restricted.value, onCheckedChange = {
                    restricted.value = it
                    onCheckedChange(app.packageName, it)
                },
                thumbContent = {
                    if (restricted.value) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                            tint = SwitchDefaults.colors().checkedTrackColor
                        )
                    }
                }
            )
        }
    )
}