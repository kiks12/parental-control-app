package com.example.parental_control_app.activities

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.parental_control_app.R
import com.example.parental_control_app.repositories.LocationRepository
import com.example.parental_control_app.workers.LocationWorker
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.circle.CircleOptions
import com.tomtom.sdk.map.display.circle.Radius
import com.tomtom.sdk.map.display.circle.RadiusUnit
import com.tomtom.sdk.map.display.ui.MapFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LocationActivity : AppCompatActivity() {

    private lateinit var mapFragment: MapFragment
    private val locationRepository = LocationRepository()

    companion object {
        const val LOCATION_PROFILE_KEY = "LOCATION_PROFILE_KEY"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_child_location)

        val kidProfileId = intent.getStringExtra("kidProfileId")

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {}
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {}
                else -> {}
            }
        }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))

        val data = Data.Builder()
            .putString(LOCATION_PROFILE_KEY, kidProfileId)
            .build()

        val worker = PeriodicWorkRequest.Builder(
            LocationWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setInputData(data).build()
        WorkManager.getInstance(this).enqueue(worker)

        val mapOptions = MapOptions(mapKey = "MpHFM3SfVoeUVYKbQTu9dIy9qPg4YrZW")
        mapFragment = MapFragment.newInstance(mapOptions)

        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()

        lifecycleScope.launch {
            var location : GeoPoint? = null
            async { location = locationRepository.getProfileLocation(kidProfileId!!) }.await()
            async {
                if (location == null) return@async

                mapFragment.getMapAsync{ map ->
                    map.addCircle(
                        CircleOptions(
                            coordinate = location!!,
                            radius = Radius(10.0, RadiusUnit.DensityPixel)
                        )
                    )
                    map.moveCamera(
                        CameraOptions(
                            position = location,
                            zoom = 15.0
                        )
                    )
                }
            }.await()
        }
    }
}