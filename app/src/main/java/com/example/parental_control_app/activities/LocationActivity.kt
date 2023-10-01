package com.example.parental_control_app.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.parental_control_app.R
import com.example.parental_control_app.repositories.LocationRepository
import com.example.parental_control_app.workers.LocationWorker
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
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

class LocationActivity : AppCompatActivity(), View.OnClickListener {

    private val mapOptions = MapOptions(mapKey = "MpHFM3SfVoeUVYKbQTu9dIy9qPg4YrZW")
    private lateinit var mapFragment: MapFragment
    private lateinit var mapFragmentContainer : FragmentContainerView
    private lateinit var progressIndicator: CircularProgressIndicator
    private val locationRepository = LocationRepository()

    companion object {
        const val LOCATION_PROFILE_KEY = "LOCATION_PROFILE_KEY"
        const val PERMISSION_LOCATION_KEY = 10002
    }

    private fun isLocationPermissionGranted() : Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            return false
        return true
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_LOCATION_KEY
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val kidProfileId = intent.getStringExtra("kidProfileId").toString()

        when(requestCode) {
            PERMISSION_LOCATION_KEY -> {
                permissions.forEachIndexed{ index, _ ->
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                        progressIndicator.visibility = View.GONE
                        mapFragmentContainer.visibility = View.VISIBLE
                        initializeMap()
                        createPeriodicLocationWorker(kidProfileId)
                        getChildLocation(kidProfileId)
                    }
                }
            }
            else -> {}
        }
    }

    private fun createPeriodicLocationWorker(kidProfileId: String) {
        val data = Data.Builder()
            .putString(LOCATION_PROFILE_KEY, kidProfileId)
            .build()

        val periodicLocationWorker = PeriodicWorkRequest.Builder(
            LocationWorker::class.java,
            15,
            TimeUnit.MINUTES,
        ).setInputData(data).build()

        // USE FOR TESTING
//        val oneTimeLocationWorker = OneTimeWorkRequest.Builder(LocationWorker::class.java)
//            .setInputData(data)
//            .build()

        WorkManager.getInstance(this).enqueue(periodicLocationWorker)
    }

    private fun initializeMap() {
        mapFragment = MapFragment.newInstance(mapOptions)
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()
    }

    private fun getChildLocation(kidProfileId: String) {
        lifecycleScope.launch {
            var location : GeoPoint? = null
            async { location = locationRepository.getProfileLocation(kidProfileId) }.await()
            async {
                if (location == null) return@async

                mapFragment.getMapAsync{ map ->
                    map.addCircle(
                        CircleOptions(
                            coordinate = location!!,
                            radius = Radius(10.0, RadiusUnit.DensityPixel),
                        )
                    )
                    map.moveCamera(
                        CameraOptions(
                            position = location!!,
                            zoom = 15.0
                        )
                    )
                }
            }.await()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_child_location)
        val kidProfileId = intent.getStringExtra("kidProfileId").toString()

        progressIndicator = findViewById(R.id.progress_circular)
        val textView = findViewById<MaterialTextView>(R.id.permissionText)
        val permissionButton = findViewById<MaterialButton>(R.id.permissionButton)
        mapFragmentContainer = findViewById(R.id.map_container)
        permissionButton.setOnClickListener(this)

        when (isLocationPermissionGranted()) {
            false -> {
                requestLocationPermission()
                progressIndicator.visibility = View.GONE
                textView.visibility = View.VISIBLE
                permissionButton.visibility = View.VISIBLE
            }
            true -> {
                progressIndicator.visibility = View.GONE
                mapFragmentContainer.visibility = View.VISIBLE
                initializeMap()
                createPeriodicLocationWorker(kidProfileId)
                getChildLocation(kidProfileId)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.permissionButton -> {
                Log.w("ON CLICK", "ON CLICK")
                requestLocationPermission()
            }
        }
    }
}