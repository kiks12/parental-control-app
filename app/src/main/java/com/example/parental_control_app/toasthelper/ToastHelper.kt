package com.example.parental_control_app.toasthelper

import android.content.Context
import android.widget.Toast

class ToastHelper(private val context: Context) {

    fun makeToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}