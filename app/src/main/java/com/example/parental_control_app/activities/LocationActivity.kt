package com.example.parental_control_app.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.parental_control_app.R
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.LocationRepository
import com.example.parental_control_app.repositories.users.UserProfile
import com.example.parental_control_app.workers.LocationWorker
import com.tomtom.quantity.Distance
import com.tomtom.sdk.common.Result
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProviderConfig
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.common.WidthByZoom
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.route.RouteOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import com.tomtom.sdk.routing.RoutePlanner
import com.tomtom.sdk.routing.online.OnlineRoutePlanner
import com.tomtom.sdk.routing.options.Itinerary
import com.tomtom.sdk.routing.options.RouteInformationMode
import com.tomtom.sdk.routing.options.RoutePlanningOptions
import com.tomtom.sdk.vehicle.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class LocationActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable {}
    private val checkInterval = 1000 * 60 * 5 // minutes
    private var firstFire = true

    private val mapOptions = MapOptions(
        mapKey = "ADmBZ6RaLW61babmRsAMAmfHALfbVw5u",
        cameraOptions = CameraOptions(zoom = 16.0)
    )
    private lateinit var routePlanner : RoutePlanner

    private lateinit var mapFragment: MapFragment
    private lateinit var tomtomMap: TomTomMap
    private lateinit var locationProvider: LocationProvider

    private val locationRepository = LocationRepository()

    private lateinit var kidProfileId : String
    private var profile : UserProfile? = null

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

        when(requestCode) {
            PERMISSION_LOCATION_KEY -> {
                permissions.forEachIndexed{ index, _ ->
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                        monitorLocation()
                    }
                }
            }
            else -> {}
        }
    }

//
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

    private fun getChildLocation(kidProfileId: String) {
        mapFragment.lifecycleScope.launch(Dispatchers.IO) {
            var kidLocation : GeoPoint? = null
            Log.w("LOCATION PROFILE PARENT", "GET")
            async { kidLocation = locationRepository.getProfileLocation(kidProfileId) }.await()
            async {
                if (kidLocation != null) {
                    val start = GeoPoint(tomtomMap.currentLocation?.position?.latitude!!, tomtomMap.currentLocation?.position?.longitude!!)
                    val end = GeoPoint(kidLocation?.latitude!!, kidLocation?.longitude!!)
                    val routePlanningOptions = RoutePlanningOptions(
                        itinerary = Itinerary(start, end),
                        vehicle = Vehicle.Car(),
                        mode = RouteInformationMode.FirstIncrement
                    )

                    when (val result = routePlanner.planRoute(routePlanningOptions)) {
                        is Result.Success -> {
                            val route = result.value().routes[0]
                            val routeOptions = RouteOptions(
                                geometry = route.geometry,
                                departureMarkerVisible = true,
                                destinationMarkerVisible = true,
                                widths = listOf(WidthByZoom(5.0)),
                                progress = Distance.meters(1000.0)
                            )
                            tomtomMap.addRoute(routeOptions)
                        }
                        is Result.Failure -> {}
                    }
                }
            }.await()
        }
    }
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_child_location)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        profile = SharedPreferencesManager.getProfile(sharedPreferences)

        kidProfileId = intent.getStringExtra("kidProfileId").toString()

        if (!isLocationPermissionGranted()) {
            requestLocationPermission()
        }

        mapFragment = MapFragment.newInstance(mapOptions)
        routePlanner = OnlineRoutePlanner.create(applicationContext, "ADmBZ6RaLW61babmRsAMAmfHALfbVw5u")
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()

        val androidLocationProviderConfig = AndroidLocationProviderConfig (
            minTimeInterval = 250L.milliseconds,
            minDistance = Distance.meters(15.0)
        )

        locationProvider = AndroidLocationProvider (
            context = applicationContext,
            config = androidLocationProviderConfig
        )

        if (profile != null && profile?.child!!) {
            createPeriodicLocationWorker(kidProfileId)
        }

        mapFragment.getMapAsync { map: TomTomMap ->
            tomtomMap = map
            tomtomMap.setLocationProvider(locationProvider)

            locationProvider.enable()

            val locationMarker = LocationMarkerOptions(
                type = LocationMarkerOptions.Type.Chevron,
            )

            tomtomMap.enableLocationMarker(locationMarker)

            tomtomMap.moveCamera(
                CameraOptions(
                    position = GeoPoint(tomtomMap.currentLocation?.position?.latitude!!, tomtomMap.currentLocation?.position?.longitude!!),
                    zoom = 14.0
                )
            )

            monitorLocation()
        }
    }

    private fun monitorLocation() {
        runnable = Runnable {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (profile == null) return@Runnable

                if (profile?.parent!!) {
                    getChildLocation(kidProfileId)
                }
            }

            monitorLocation()
        }

        if (firstFire)  {
            handler.postDelayed(runnable, 1000L)
            firstFire = false
        } else {
            handler.postDelayed(runnable, checkInterval.toLong())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (profile != null && profile?.child!!) {
            lifecycleScope.launch {
//                Log.w("LOCATION SAVER DESTROY", "SAVE")
                val latitude = tomtomMap.currentLocation?.position?.latitude!!
                val longitude = tomtomMap.currentLocation?.position?.latitude!!

                locationRepository.saveLocation(
                    kidProfileId,
                    GeoPoint(latitude, longitude)
                )
            }
        }
    }
}