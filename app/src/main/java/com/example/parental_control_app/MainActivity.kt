package com.example.parental_control_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.parental_control_app.activities.LoginActivity
import com.example.parental_control_app.repositories.AppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

@Suppress("unused")
data class AppRestriction(
    val label: String,
    val packageName: String,
    val age: Int,
) {
    constructor() : this("", "", 0)
}

//private fun readCsv(inputStream: InputStream) : List<AppRestriction> {
//    val reader = inputStream.bufferedReader()
//    val header = reader.readLine()
//
//    return reader.lineSequence()
//        .filter { it.isNotBlank() }
//        .map {
//            val (label, packageName, _, age) = it.split(',', ignoreCase = false, limit = 4)
//            AppRestriction(label.trim(), packageName.trim(), age.trim().removeSurrounding("\"").toInt())
//        }.toList()
//}

class MainActivity : AppCompatActivity() {

    private val appsRepository = AppsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*

        DO NOT TOUCH
        only uncomment this when apps_age_restrictions.csv file is changed

        this block will save the app restrictions suggestion data in the cloud

        */

//        val inputStream = assets.open("final.csv")
//        val appList = readCsv(inputStream)
//
//        Log.w("APP RESTRICTIONS", appList.toString())

//        lifecycleScope.launch(Dispatchers.IO) {
//            appsRepository.saveAppRestrictions(appList)
//        }

        startActivity(Intent(this, LoginActivity::class.java))
    }
}