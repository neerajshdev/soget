package com.gd.reelssaver.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.gd.reelssaver.FirebaseKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppOpenAdManager(activity: Activity) : Application.ActivityLifecycleCallbacks {
    private val TAG = AppOpenAdManager::class.simpleName
    private var isInBackground = false
    private var currentActivity: Activity? = activity
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private val isShowingAd = AtomicBoolean(false)
    private val adUnit by lazy {  Firebase.remoteConfig.getString(FirebaseKeys.APP_OPEN_AD_UNIT_ID).trim() }

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            Log.e(TAG, "ON_RESUME: showOpen Ad")
            isInBackground = false
            scope.launch {
                if (currentActivity == null) {
                    Log.d(TAG, "No reference to current activity.")
                }
                currentActivity?.let { showNextAd(it) }
            }
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            Log.e("APP", "paused")
            isInBackground = true
        }
    }

    init {
        Log.e(TAG, "Ad Open ID : $adUnit" )
        activity.application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "onActivityStarted: $activity")
        if (activity is AdActivity) {
            Log.d(TAG, "isShowing ad: true")
            isShowingAd.set(true)
        } else {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {
        if (activity is AdActivity) {
            isShowingAd.set(false)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "onActivityStopped: $activity")
        if (activity is AdActivity) {
            isShowingAd.set(false)
            Log.d(TAG, "isShowingAd: $isShowingAd")
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (activity === currentActivity) {
            currentActivity = null
        }
    }


    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private var loadTime: Long = 0

    suspend fun loadAd(context: Context) : AppOpenAd? {
        isLoadingAd = true
        val request = AdRequest.Builder().build()

        return suspendCoroutine { cont ->
            AppOpenAd.load(
                context,
                adUnit,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {

                    override fun onAdLoaded(ad: AppOpenAd) {
                        isLoadingAd = false
                        loadTime = Date().time
                        Log.d(TAG, "onAdLoaded.")
                        //Toast.makeText(context, "onAdLoaded", Toast.LENGTH_SHORT).show()

                        cont.resume(ad)
                    }


                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoadingAd = false
                        Log.d(TAG, "onAdFailedToLoad: " + loadAdError.message)
                        //Toast.makeText(context, "onAdFailedToLoad", Toast.LENGTH_SHORT).show()
                        cont.resume(null)
                    }
                }
            )
        }
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }



    private fun showNextAd(activity: Activity) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd.get()) {
            Log.d(TAG, "The app open ad is already showing.")
            return
        }

        if (isLoadingAd) return

        if (!isAdAvailable()) {
            Log.d(TAG, "The app open ad is not ready yet.")
            scope.launch {
                appOpenAd = loadAd(activity)?.also {
                    Log.e(TAG, "App open ad is ready to show.",)
                }
            }
            return
        }

        if (appOpenAd == null) return
        if (isInBackground) return

        appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            /** Called when full screen content is dismissed. */
            override fun onAdDismissedFullScreenContent() {
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null
                Log.d(TAG, "onAdDismissedFullScreenContent.")
                /* Toast.makeText(activity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
                             .show()*/
                scope.launch { loadAd(activity)?.also { appOpenAd = it } }
            }

            /** Called when fullscreen content failed to show. */
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                Log.d(TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                scope.launch { appOpenAd = loadAd(activity) }
            }

            /** Called when fullscreen content is shown. */
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "onAdShowedFullScreenContent.")
                /*Toast.makeText(activity, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
                            .show()*/
            }
        }
        appOpenAd!!.show(activity)
    }
}