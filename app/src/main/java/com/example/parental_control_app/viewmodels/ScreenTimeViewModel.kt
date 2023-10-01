package com.example.parental_control_app.viewmodels

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.DonutChartData
import com.example.parental_control_app.data.DonutChartDataCollection
import com.example.parental_control_app.data.Response
import com.example.parental_control_app.data.ResponseStatus
import com.example.parental_control_app.repositories.AppsRepository
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScreenTimeViewModel(
    private val kidProfileId: String,
) : ViewModel(){

    private val appsRepository: AppsRepository = AppsRepository()
    private val usersRepository: UsersRepository = UsersRepository()

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
    private val _uidState = mutableStateOf("")
    private val _screenTimeLimitState = mutableStateMapOf(
        "HOURS" to "0",
        "MINUTES" to "0",
        "SECONDS" to "0",
    )

    val screenTimeLimitState : Map<String, String>
        get() = _screenTimeLimitState

    val loadingState : Boolean
        get() = _loadingState.value

    val donutChartData : DonutChartDataCollection
        get() = _donutChartState.value

    init {
        viewModelScope.launch {
            _loadingState.value = true
            val uid = usersRepository.getProfileUID(kidProfileId)
            val screenTimeLimit = usersRepository.getChildScreenTimeLimit(uid)
            val screenTimeLimitData = screenTimeLimit?.data

            _screenTimeLimitState["HOURS"] = screenTimeLimitData?.getValue("HOURS").toString()
            _screenTimeLimitState["MINUTES"] = screenTimeLimitData?.getValue("MINUTES").toString()
            _screenTimeLimitState["SECONDS"] = screenTimeLimitData?.getValue("SECONDS").toString()

            val apps = appsRepository.getApps(uid)
            val sortedApps = apps.sortedBy { app -> app.screenTime }.asReversed()

            withContext(Dispatchers.Default) {
                for (index in 0..4) {
                    _donutChartState.value = _donutChartState.value.copy(
                        items = _donutChartState.value.items.plus(
                            DonutChartData(sortedApps[index].screenTime.toFloat(), COLORS[index], sortedApps[index].label)
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
            }
            withContext(Dispatchers.Main) {
                async { _loadingState.value = false }.await()
                async { _uidState.value = uid }.await()
            }
        }
    }

    suspend fun setChildScreenTimeLimit(limit: Long) : Response? {
        val response = CompletableDeferred<Response?>(null)

        viewModelScope.launch {
            val res = usersRepository.setChildScreenTimeLimit(_uidState.value, limit)
            response.complete(res)
        }

        return response.await()
    }

    fun setScreenTimeState(time: String, type: String) {
        _screenTimeLimitState[type] = time
    }

    suspend fun removeScreenTimeLimit() : Response? {
        val response = CompletableDeferred<Response?>(null)

        viewModelScope.launch {
            val res = usersRepository.setChildScreenTimeLimit(_uidState.value, 0, true)
            if (res?.status == ResponseStatus.SUCCESS) {
                _screenTimeLimitState.keys.forEach{ key ->
                    _screenTimeLimitState[key] = "0"
                }
            }
            response.complete(res)
        }

        return response.await()
    }
}