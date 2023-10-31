package com.njsh.reelssaver.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.reelssaver.FirebaseKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("StaticFieldLeak")
object InterstitialAdManager {
    private val TAG = InterstitialAdManager::class.simpleName
    private val adUnit: String by lazy { Firebase.remoteConfig.getString(FirebaseKeys.INTERSTITIAL_AD_UNIT_ID) }
    private var currentActivity: Activity? = null
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private var isShowingAd = AtomicBoolean(false)
    private var isInBackground: Boolean = false
    private var isAdLoading: Boolean = false

    private var lastAdLoadTime = 0L
    private var interstitialAd: InterstitialAd? = null
    private var adCount = 0
    private var showAdOnClick : Int = 0

    private val activityLifeCycleCallback = object : ActivityLifeCycleCallback() {
        override fun onActivityStarted(p0: Activity) {
            Log.d(TAG, "onActivityStopped: $p0")
            if (p0 is AdActivity) {
                isShowingAd.set(true)
                Log.d(TAG, "isShowingAd: $isShowingAd")
            } else {
                currentActivity = p0
            }
        }

        override fun onActivityStopped(p0: Activity) {
            Log.d(TAG, "onActivityStopped: $p0")
            if (p0 is AdActivity) {
                isShowingAd.set(false)
                Log.d(TAG, "isShowingAd: $isShowingAd")
            }
        }

        override fun onActivityPaused(p0: Activity) {}

        override fun onActivityDestroyed(p0: Activity) {
            if (p0 === currentActivity) {
                currentActivity = null
            }
        }

    }

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            isInBackground = false
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            Log.e("APP", "paused")
            isInBackground = true
        }
    }

    fun init(activity: Activity) {
        activity.application.registerActivityLifecycleCallbacks(activityLifeCycleCallback)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
        showAdOnClick = Firebase.remoteConfig.getLong(FirebaseKeys.CLICK_COUNT).toInt()
        currentActivity = activity
    }


    fun willShowAd(): Boolean {
        adCount += 1
        return if (isAdAvailable()) {
            adCount % showAdOnClick == 0 && isShowingAd.get().not() && isInBackground.not()
        } else {
            scope.launch { interstitialAd = loadNextAd() }
            false
        }
    }

    fun showAd() = scope.launch {
        if (isShowingAd.get()) {
            Log.e(TAG, "Ad is already open")
            return@launch
        }

        if (!isAdAvailable()) {
            interstitialAd = loadNextAd()
            return@launch
        }

        if (isInBackground) return@launch

        if (interstitialAd != null && currentActivity != null) {
            interstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    scope.launch {
                        interstitialAd = null
                        interstitialAd = loadNextAd()
                    }
                }
            }
            interstitialAd!!.show(currentActivity!!)
        }
    }


    private fun isAdAvailable(): Boolean {
        return interstitialAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - lastAdLoadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private suspend fun loadNextAd(): InterstitialAd? {
        val adRequest = AdRequest.Builder().build()

        return suspendCoroutine { cont ->
            currentActivity?.let {
                isAdLoading = true

                Log.e(TAG, "Loading Ad")

                InterstitialAd.load(it, adUnit, adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        cont.resume(null)
                        Log.e(TAG, "Failed to load Ad")
                    }

                    override fun onAdLoaded(p0: InterstitialAd) {
                        cont.resume(p0)
                        Log.e(TAG, "Ad Loaded")
                        lastAdLoadTime = Date().time
                    }
                })
            }
        }.also {
            isAdLoading = false
        }
    }
}