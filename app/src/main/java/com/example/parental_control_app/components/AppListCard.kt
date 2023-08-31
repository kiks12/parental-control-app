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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.parental_control_app.data.UserApps
import java.util.concurrent.TimeUnit

@Composable
fun AppCard(
    app: UserApps,
    appIcon: String,
    totalScreenTime: Long,
    onCheckedChange: (appName: String, newRestriction: Boolean) -> Unit,
) {

    val restricted = remember { mutableStateOf(app.restricted) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp, horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        AsyncImage(model = appIcon, contentDescription = app.name)
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = app.name,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (app.screenTime.toInt() == 0) {
                    LinearProgressIndicator(progress = 0f)
                    Text("00:00:00", fontSize = 10.sp)
                } else {
                    LinearProgressIndicator(progress = app.screenTime.toFloat() / totalScreenTime)
                    Text(String.format("%d:%d:%d",
                        TimeUnit.MILLISECONDS.toHours(app.screenTime),
                        TimeUnit.MILLISECONDS.toMinutes(app.screenTime),
                        TimeUnit.MILLISECONDS.toSeconds(app.screenTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(app.screenTime))
                    ), fontSize = 10.sp)
                }
            }
            Switch(
                checked = restricted.value, onCheckedChange = {
                    restricted.value = it
                    onCheckedChange(app.name, it)
                }
            )
        }
    }
}