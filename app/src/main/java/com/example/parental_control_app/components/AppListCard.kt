package com.example.parental_control_app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.parental_control_app.data.UserApps
import java.util.concurrent.TimeUnit

enum class AppCardType {
    SUGGESTIONS,
    APP
}

@Composable
fun AppCard(
    app: UserApps,
    appIcon: String,
    totalScreenTime: Long,
    type: AppCardType,
    onCheckedChange: (appName: String, newRestriction: Boolean) -> Unit,
) {

    val restricted = remember { mutableStateOf(app.restricted) }
    val cardColor = if (type == AppCardType.APP) Color.Transparent else MaterialTheme.colorScheme.secondaryContainer
    val linearProgressColor = if (type == AppCardType.APP) Color.LightGray else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp, end = 10.dp, start = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(50.dp)
                            .width(50.dp)
                    ) {
                        if (appIcon == "") {
                            CircularProgressIndicator()
                        } else {
                            AsyncImage(model = appIcon, contentDescription = app.packageName)
                        }
                    }
                    Column{
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            text = app.label,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Column (
                            modifier = Modifier.padding(start = 10.dp)
                        ){
                            if (app.screenTime.toInt() == 0) {
                                LinearProgressIndicator(
                                    progress = 0f,
                                    trackColor = linearProgressColor,
                                    modifier = Modifier.width(200.dp)
                                )
                                Text("00:00:00", fontSize = 10.sp)
                            } else {
                                LinearProgressIndicator(
                                    progress = app.screenTime.toFloat() / totalScreenTime,
                                    trackColor = linearProgressColor,
                                    modifier = Modifier.width(200.dp)
                                )
                                Text(String.format("%d:%d:%d",
                                    TimeUnit.MILLISECONDS.toHours(app.screenTime),
                                    TimeUnit.MILLISECONDS.toMinutes(app.screenTime),
                                    TimeUnit.MILLISECONDS.toSeconds(app.screenTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(app.screenTime))
                                ), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
            Switch(
                checked = restricted.value, onCheckedChange = {
                    restricted.value = it
                    onCheckedChange(app.packageName, it)
                }
            )
        }
    }
}