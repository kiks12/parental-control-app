package com.example.parental_control_app.activities.sms

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.util.Log
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.parental_control_app.helpers.ActivityStarterHelper
import com.example.parental_control_app.screens.sms.SmsScreen
import com.example.parental_control_app.ui.theme.ParentalcontrolappTheme
import com.example.parental_control_app.viewmodels.sms.SmsViewModel

class SmsActivity : AppCompatActivity() {

    private lateinit var activityStarterHelper: ActivityStarterHelper
    private lateinit var kidProfileId : String
    private lateinit var smsViewModel: SmsViewModel
    private var permissionList = listOf<String>()

    companion object {
        private const val SMS_PERMISSION_CODE = 11
    }

    private fun isSmsPermissionGranted() : Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED)
            permissionList = permissionList.plus(Manifest.permission.RECEIVE_SMS)

//        Log.w("SMS ACTIVITY", "Permissions ${permissionList.toList()}")

        return permissionList.isEmpty()
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(this, permissionList.toTypedArray(), SMS_PERMISSION_CODE)
    }

    private fun initializeUI() {
        activityStarterHelper = ActivityStarterHelper(this)
        kidProfileId = intent.getStringExtra("kidProfileId").toString()
        smsViewModel = SmsViewModel(kidProfileId)
        smsViewModel.addOnBackClick { finish() }
        smsViewModel.setActivityStarterHelper(activityStarterHelper)

        setContent {
            ParentalcontrolappTheme {
                SmsScreen(smsViewModel)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(isSmsPermissionGranted()) {
            true -> initializeUI()
            else -> requestSmsPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            SMS_PERMISSION_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED } ) {
//                    Log.w("SMS ACTIVITY", "All Permissions Granted")
                    initializeUI()
                } else {
                    requestSmsPermission()
                }
            }
        }
    }
}