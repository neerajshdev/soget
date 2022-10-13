package com.njsh.instadl

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig


class Application : android.app.Application()
{
    companion object
    {
        val TAG = "my app"
        val debug = true
        @SuppressLint("StaticFieldLeak")
        private lateinit var ctx: Context
        fun getAppContext(): Context
        {
            return ctx;
        }

        fun clipBoardData(): String
        {
            val clipboardManager = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            return clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
        }

        fun toast(text: String, len: Int = Toast.LENGTH_SHORT)
        {
            Toast.makeText(ctx, text, len)
                .show()
        }
    }

    override fun onCreate()
    {
        super.onCreate()
        ctx = this

        val remoteConfigSet = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(10)
            .build()

        Firebase.remoteConfig.setConfigSettingsAsync(remoteConfigSet)
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener()
        { task ->
            if (task.isComplete)
            {
                AppPref.edit {
                    putBoolean(AppPref.FIREBASE_FETCHED, true)
                }
            }
        }
    }
}