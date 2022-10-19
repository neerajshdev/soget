package com.njsh.instadl

import android.content.Context
import android.content.SharedPreferences

object AppPref
{
    private const val name = "App Shared Pref"
    val pref by lazy { App.instace().getSharedPreferences(name, Context.MODE_PRIVATE) }

    const val FIREBASE_FETCHED = "IS_FIREBASE_FETCHED"

    fun edit(prefEditor: SharedPreferences.Editor.()->Unit)
    {
        val editor = pref.edit()
        editor.prefEditor()
        editor.apply()
    }
}