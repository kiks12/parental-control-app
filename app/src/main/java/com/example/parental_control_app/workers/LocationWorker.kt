package com.example.parental_control_app.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parental_control_app.activities.LocationActivity
import com.example.parental_control_app.repositories.LocationRepository
import com.google.android.gms.location.LocationServices
import com.tomtom.sdk.location.GeoPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LocationWorker(
    private val ctx : Context,
    params: WorkerParameters,
) : CoroutineWorker(ctx, params) {

    private val locationRepository = LocationRepository()

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun doWork(): Result {
        val profileId = inputData.getString(LocationActivity.LOCATION_PROFILE_KEY).toString()
        val locationClient = LocationServices.getFusedLocationProviderClient(ctx)

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationClient.getCurrentLocation(100, null).addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener
                Log.w("LOCATION WORKER", location.toString())

                val lat = location.latitude
                val long = location.longitude
                val point = GeoPoint(lat, long)

                Log.w("LOCATION WORKER", point.toString())
                Log.w("LOCATION WORKER", profileId)


                GlobalScope.launch(Dispatchers.IO) {
                    async { locationRepository.saveLocation(profileId, point) }.await()
                    async { Log.w("LOCATION WORKER", "Location Saved") }.await()
                }
            }
        }

        return Result.success()
    }
}