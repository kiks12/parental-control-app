package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.R
import com.example.parental_control_app.screens.parent.ParentChildLocationScreen
import com.example.parental_control_app.viewmodels.parent.ParentChildLocationViewModel

class ParentChildLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parentChildLocationViewModel = ParentChildLocationViewModel()

        setContent {
            ParentChildLocationScreen(parentChildLocationViewModel)
        }
    }
}