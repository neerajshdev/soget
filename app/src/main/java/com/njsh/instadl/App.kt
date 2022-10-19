package com.njsh.instadl

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.njsh.instadl.appevent.ConnectionAvailable
import com.njsh.instadl.appevent.Event
import com.njsh.instadl.appevent.EventHandler
import com.njsh.instadl.appevent.EventManager


class App : android.app.Application()
{
    companion object
    {
        val TAG = "my app"
        val debug = true

        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: App
        fun instace(): App
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
            Toast.makeText(instance, text, len).show()
        }
    }

    override fun onCreate()
    {
        super.onCreate()
        instance = this

        val remoteConfigSet =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(10).build()

        val conEventHandler = object : EventHandler
        {
            override fun handleEvent(event: Event)
            {
                if (event is ConnectionAvailable)
                {
                    Firebase.remoteConfig.setConfigSettingsAsync(remoteConfigSet)
                    Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener() { task ->
                        if (task.isComplete)
                        {
                            AppPref.edit {
                                putBoolean(AppPref.FIREBASE_FETCHED, true)
                            }
                        }
                    }
                }
                // remove this handler
                EventManager.getInstance().removeHandler(this)
            }
        }


        // run only once
        EventManager.getInstance().addHandler(conEventHandler)
    }
}