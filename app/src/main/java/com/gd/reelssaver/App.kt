package com.gd.reelssaver

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig


class App : android.app.Application() {
    companion object {
        val TAG = com.gd.reelssaver.App::class.simpleName
        val debug = com.gd.reelssaver.BuildConfig.DEBUG

        var onMoveToForeground: () -> Unit = {}

        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: com.gd.reelssaver.App
        fun instance(): com.gd.reelssaver.App {
            return com.gd.reelssaver.App.Companion.instance;
        }

        fun clipBoardData(): String {
            val clipboardManager =
                com.gd.reelssaver.App.Companion.instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            return clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
        }

        fun toast(text: String, len: Int = Toast.LENGTH_SHORT) {
            val handler = Handler(Looper.getMainLooper())
            handler.post() {
                Toast.makeText(com.gd.reelssaver.App.Companion.instance, text, len).show()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        com.gd.reelssaver.App.Companion.instance = this

        setupFirebase()
        MobileAds.initialize(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            var isLaunch = true
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        if (com.gd.reelssaver.App.Companion.debug) println("APPLICATION RUNNING IN FOREGROUND")
                        if (!isLaunch) com.gd.reelssaver.App.Companion.onMoveToForeground() else isLaunch = false
                    }
                    else -> {}
                }
            }
        })
    }


    private fun setupFirebase() {
        FirebaseApp.initializeApp(this)
        val remoteConfigSet =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(10).build()
        Firebase.remoteConfig.setConfigSettingsAsync(remoteConfigSet)
    }

  /*  fun logEventVpnConnected() {
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("vpnConnected", "YES")
        analytics.logEvent("vpn", bundle)
    }

    fun logEventVpnFailedToConnect() {
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("vpnConnected", "NO")
        analytics.logEvent("vpn", bundle)
    }*/
}