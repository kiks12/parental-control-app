package com.example.parental_control_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.parental_control_app.activities.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = "app_locker"
        val descriptionText = "channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("app_locker_channel", name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

//        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
//            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//            putExtra(Settings.EXTRA_CHANNEL_ID, mChannel.getId())
//        }
//        startActivity(intent)



        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
        finish()
    }
}