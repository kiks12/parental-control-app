package com.example.parental_control_app.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.DonutChartData
import com.example.parental_control_app.data.DonutChartDataCollection
import com.example.parental_control_app.repositories.AppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScreenTimeViewModel(
    private val profileId: String,
    private val appsRepository: AppsRepository = AppsRepository()
) : ViewModel(){

    companion object {
        private val COLORS = listOf(
            Color(220, 138, 107, 255),
            Color(82, 101, 222, 255),
            Color(96, 102, 145, 255),
            Color(161, 219, 121, 255),
            Color(143, 79, 56, 255),
            Color(232, 102, 113, 255),
        )
    }

    private val _donutChartState = mutableStateOf(DonutChartDataCollection(listOf()))
    private val _loadingState = mutableStateOf(true)
    lateinit var onBackClick : () -> Unit

    val loadingState : Boolean
        get() = _loadingState.value

    val donutChartData : DonutChartDataCollection
        get() = _donutChartState.value

    init {
        viewModelScope.launch(Dispatchers.IO){
            async { _loadingState.value = true }.await()
            async {
                val apps = appsRepository.getApps(profileId)
                val sortedApps = apps.sortedBy { app -> app.screenTime }.asReversed()
                for (index in 0..4) {
                    _donutChartState.value = _donutChartState.value.copy(
                        items = _donutChartState.value.items.plus(
                            DonutChartData(sortedApps[index].screenTime.toFloat(), COLORS[index], sortedApps[index].name)
                        )
                    )
                }
                var othersScreenTime = 0.0
                for (index in 5 until sortedApps.size) othersScreenTime += sortedApps[index].screenTime
                _donutChartState.value = _donutChartState.value.copy(
                    items = _donutChartState.value.items.plus(
                        DonutChartData(othersScreenTime.toFloat(), COLORS[COLORS.size-1], "Others")
                    )
                )
            }.await()
            async { _loadingState.value = false }.await()
        }
    }

    fun addOnBackClick(callback: () -> Unit) {
        onBackClick = callback
    }
}