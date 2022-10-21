package com.njsh.instadl

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.instadl.appevent.ConnectionAvailable
import com.njsh.instadl.appevent.Event
import com.njsh.instadl.appevent.EventHandler
import com.njsh.instadl.appevent.EventManager
import com.njsh.instadl.util.NetworkMonitoringUtil
import com.njsh.instadl.util.isOnline


class App : android.app.Application()
{
    companion object
    {
        val TAG = "my app"
        val debug = true

        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: App
        fun instance(): App
        {
            return instance;
        }

        fun clipBoardData(): String
        {
            val clipboardManager =
                instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            return clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
        }

        fun toast(text: String, len: Int = Toast.LENGTH_SHORT)
        {
            val handler = Handler(Looper.getMainLooper())
            handler.post() {
                Toast.makeText(instance, text, len).show()
            }
        }
    }

    override fun onCreate()
    {
        super.onCreate()
        instance = this

        setupFirebase()
        MobileAds.initialize(this)
    }


    private fun setupFirebase()
    {
        FirebaseApp.initializeApp(this)
        val remoteConfigSet =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(10).build()
        Firebase.remoteConfig.setConfigSettingsAsync(remoteConfigSet)
    }
}