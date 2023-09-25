package com.example.parental_control_app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

enum class TimeLimit(val value: String, val limit: Long) {
    ALWAYS(value = "Always", limit = 0L),
    ONE_HOUR(value = "One Hour", limit = TimeUnit.HOURS.toMillis(1)),
    TWO_HOURS(value = "Two Hours", limit = TimeUnit.HOURS.toMillis(2)),
    THREE_HOURS(value = "Three Hours", limit = TimeUnit.HOURS.toMillis(3)),
    CUSTOM(value = "Custom", limit = 1L),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCard(
    app: UserApps,
    appIcon: String,
    type: AppCardType,
    onCheckedChange: (appName: String, newRestriction: Boolean) -> Unit,
    onTimeLimitChange: (appName: String, newTimeLimit: Long) -> Unit,
) {

    val restricted = remember { mutableStateOf(app.restricted) }
    var expanded by remember { mutableStateOf(false) }
    var limit by remember { mutableStateOf(
        when (app.limit) {
            TimeLimit.ALWAYS.limit -> TimeLimit.ALWAYS
            TimeLimit.ONE_HOUR.limit -> TimeLimit.ONE_HOUR
            TimeLimit.TWO_HOURS.limit -> TimeLimit.TWO_HOURS
            TimeLimit.THREE_HOURS.limit -> TimeLimit.THREE_HOURS
            else -> TimeLimit.CUSTOM
        }
    ) }

//    val timeLimit = if (app.limit == 0L) "Always" else String.format("%d:%d:%d",
//            TimeUnit.MILLISECONDS.toHours(app.limit),
//            TimeUnit.MILLISECONDS.toMinutes(app.screenTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(app.screenTime)),
//            TimeUnit.MILLISECONDS.toSeconds(app.screenTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(app.screenTime))
//        )

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
            Column {
                if (app.screenTime.toInt() == 0) {
                    Text("00:00:00")
                } else {
                    Text(String.format("%d:%d:%d",
                        TimeUnit.MILLISECONDS.toHours(app.screenTime),
                        TimeUnit.MILLISECONDS.toMinutes(app.screenTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(app.screenTime)),
                        TimeUnit.MILLISECONDS.toSeconds(app.screenTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(app.screenTime))
                    ))
                }
                if (restricted.value) {
                    Text(
                        "Set Time Limit - ${limit.value}",
                        modifier = Modifier.clickable { expanded = true }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        TimeLimit.values().forEach { timeLimit ->
                            DropdownMenuItem(
                                text = { Text(timeLimit.value) },
                                onClick = {
                                    limit = timeLimit
                                    expanded = false
                                    onTimeLimitChange(app.packageName, timeLimit.limit)
                                  },
                                trailingIcon = {
                                    if (timeLimit.limit == limit.limit) {
                                        Icon(Icons.Filled.Check, "Check")
                                    }
                                }
                            )
                        }
                    }
                }
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

