package com.example.parental_control_app.helpers

class ScreenTimeHelper {
    private var totalScreenTime = 0f

    fun getTotalScreenTime() : Float {
        return totalScreenTime
    }

    fun addScreenTime(screenTime: Float) {
        totalScreenTime += screenTime
    }
}