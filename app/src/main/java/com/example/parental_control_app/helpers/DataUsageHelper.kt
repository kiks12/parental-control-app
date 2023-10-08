//package com.example.parental_control_app.helpers
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.usage.NetworkStats
//import android.app.usage.NetworkStatsManager
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.RemoteException
//import android.telephony.TelephonyManager
//import android.util.Log
//import androidx.core.app.ActivityCompat
//
//class DataUsageHelper(val context: Context) {
//
//    @SuppressLint("HardwareIds")
//    fun getDataUsage(context: Context, startTime: Long, endTime: Long): Long {
//        val networkStatsManager =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
//            } else {
//                TODO("VERSION.SDK_INT < M")
//            }
//        val telephonyManager =
//            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        val subscriberId = telephonyManager.subscriberId
//
//        try {
//            val networkType = if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.READ_PHONE_STATE
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return 0L
//            } else { }
//
//            if (telephonyManager.isDataEnabled) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    NetworkStats.Bucket.METERED_ALL
//                } else {
//                    TODO("VERSION.SDK_INT < O")
//                }
//            } else {
//                NetworkStats.Bucket.UNMETERED_ALL
//            }
//
//            val networkStats = networkStatsManager.querySummary(
//                networkType,
//                subscriberId,
//                startTime,
//                endTime
//            )
//
//            var totalDataUsage: Long = 0
//
//            while (networkStats.hasNextBucket()) {
//                val bucket = NetworkStats.Bucket()
//                networkStats.getNextBucket(bucket)
//                totalDataUsage += bucket.rxBytes + bucket.txBytes
//            }
//
//            return totalDataUsage
//        } catch (e: RemoteException) {
//            Log.e("DataUsageHelper", "Error querying data usage: ${e.message}")
//            e.printStackTrace()
//            return -1
//        }
//    }
//}
