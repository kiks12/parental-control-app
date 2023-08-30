package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parental_control_app.R
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.ui.MapFragment

class ParentChildLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_child_location)

//        val parentChildLocationViewModel = ParentChildLocationViewModel()

        val mapOptions = MapOptions(mapKey = "MpHFM3SfVoeUVYKbQTu9dIy9qPg4YrZW")
        val mapFragment = MapFragment.newInstance(mapOptions)

        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()

        mapFragment.getMapAsync { tomtomMap: TomTomMap ->
//            /* Your code goes here */
        }

//        setContent {
//            ParentChildLocationScreen(parentChildLocationViewModel)
//        }
    }
}