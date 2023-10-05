package com.example.parental_control_app.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.parental_control_app.data.ActivityLog
import com.example.parental_control_app.data.Response
import com.example.parental_control_app.data.ResponseStatus
import com.example.parental_control_app.data.UserApps
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

data class ActivityLogGetterResult(
    val activityLogs : List<ActivityLog>,
    val icons : Map<String, String>
)

class ActivityLogRepository {

    private val db = Firebase.firestore
    private val appsRepository = AppsRepository()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getChildActivityLog(uid: String, timestamp: Long) : ActivityLogGetterResult {
        val completable = CompletableDeferred<ActivityLogGetterResult>()

        coroutineScope {
            launch(Dispatchers.IO){
                val specificDateTimestamp = Timestamp(Date(timestamp))

                val calendar = Calendar.getInstance()
                calendar.time = specificDateTimestamp.toDate()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDayTimestamp = Timestamp(calendar.time)

                calendar.add(Calendar.DAY_OF_MONTH, 1)
                val endOfDayTimestamp = Timestamp(calendar.time)

                val docs = db.collection("profiles/$uid/activityLog")
                    .whereGreaterThan("datetime", startOfDayTimestamp)
                    .whereLessThan("datetime", endOfDayTimestamp)
                    .orderBy("datetime", Query.Direction.DESCENDING)
                    .get().await()

                val apps = docs.documents.map { document -> UserApps(packageName = document.data?.get("packageName").toString()) }.toList().distinct()
                val icons = appsRepository.getAppIcons(uid, apps)

                Log.w("APP ICONS", icons.toString())

                completable.complete(
                    ActivityLogGetterResult(
                        activityLogs = docs.toObjects(ActivityLog::class.java),
                        icons = icons
                    )
                )
            }
        }

        return completable.await()
    }

    suspend fun addActivityLog(uid: String, activity: ActivityLog) : Response {
        val responseCompletable = CompletableDeferred<Response>(null)

        coroutineScope {
            launch(Dispatchers.IO) {
                val document = db.collection("profiles/$uid/activityLog").document()
                document.set(activity)
                    .addOnSuccessListener {
                        responseCompletable.complete(
                            Response(
                                status = ResponseStatus.SUCCESS,
                                message = "Successfully logged activity"
                            )
                        )
                    }
                    .addOnFailureListener {
                        it.localizedMessage?.let { it1 ->
                            Response(
                                status = ResponseStatus.FAILED,
                                message = it1
                            )
                        }?.let { it2 ->
                            responseCompletable.complete(
                                it2
                            )
                        }
                    }
            }
        }

        return responseCompletable.await()
    }
}