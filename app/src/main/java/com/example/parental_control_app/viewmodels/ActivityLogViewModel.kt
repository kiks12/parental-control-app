package com.example.parental_control_app.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parental_control_app.data.ActivityLog
import com.example.parental_control_app.repositories.ActivityLogRepository
import com.example.parental_control_app.repositories.users.UsersRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar


class ActivityLogViewModel(
    private val profileId: String
) : ViewModel() {

    private val usersRepository = UsersRepository()
    private val activityLogRepository = ActivityLogRepository()

    private val _uidState = mutableStateOf("")
    private val _logsState = mutableStateOf<List<ActivityLog>>(listOf())
    private val _iconsState = mutableStateOf<Map<String, String>>(mapOf())
    private val _selectedDate = mutableLongStateOf(0)
    private val _loadingState = mutableStateOf(true)
    private val _dateToday = mutableLongStateOf(0L)

    val logsState : List<ActivityLog>
        get() = _logsState.value

    val iconsState : Map<String, String>
        get() = _iconsState.value

    val selectedDate : Long
        get() = _selectedDate.longValue

    val loadingState : Boolean
        get() = _loadingState.value

    val dateToday : Long
        get() = _dateToday.longValue

    init {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val currentDate = Calendar.getInstance()
                _selectedDate.longValue = currentDate.timeInMillis
                _dateToday.longValue = currentDate.timeInMillis
                val uid = usersRepository.getProfileUID(profileId)
                val result = activityLogRepository.getChildActivityLog(uid, currentDate.timeInMillis)
                _logsState.value = result.activityLogs
                _iconsState.value = result.icons
                _uidState.value = uid
                async { _loadingState.value = false }.await()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshLogs(selectedDate: Long) {
        viewModelScope.launch {
            async { _loadingState.value = true }.await()
            _selectedDate.longValue = selectedDate
            val result = activityLogRepository.getChildActivityLog(_uidState.value, selectedDate)
            _logsState.value = result.activityLogs
            _iconsState.value = result.icons
            async { _loadingState.value = false }.await()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun resetDateToday() {
        _selectedDate.longValue = _dateToday.longValue
        viewModelScope.launch {
            refreshLogs(_selectedDate.longValue)
        }
    }

}