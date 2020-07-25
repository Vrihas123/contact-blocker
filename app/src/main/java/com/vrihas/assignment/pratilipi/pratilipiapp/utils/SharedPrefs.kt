package com.vrihas.assignment.pratilipi.pratilipiapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

/*
    Custom Shared Preferences file
 */

class SharedPrefs(context: Context) {

    private val PREF_NAME = "PratilipiSharedPreferences"
    private val KEY_IS_FIRST = "isFirst"
    var PRIVATE_MODE = 0

    var pref: SharedPreferences? = null
    var editor: Editor? = null

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref!!.edit()
    }


    fun setFirst(isLoggedIn: Boolean) {
        editor!!.putBoolean(KEY_IS_FIRST, isLoggedIn)
        editor!!.commit()
    }

    fun isFirst(): Boolean {
        return pref!!.getBoolean(KEY_IS_FIRST, true)
    }
}