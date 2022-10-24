package com.njsh.instadl.ads

import android.app.Activity
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.njsh.instadl.util.CallResult


fun loadAppOpenAd(activity: Activity, action: () -> Unit) {
    AppOpenAdLoader.takeAndLoad { result ->
        when (result) {
            is com.njsh.instadl.api.CallResult.Success -> {
                result.data.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        action()
                    }
                }
                result.data.show(activity)
            }
            is com.njsh.instadl.api.CallResult.Failed -> {
                action()
            }
        }
    }
}

/**
 * Show the interstitial ad if failed, then provided action will be executed
 */
fun checkAndShowAd(activity: Activity, action: () -> Unit) {
    if (AdClickCounter.check()) {
        InterstitialAdLoader.takeAndLoad(object : CallResult<InterstitialAd> {
            override fun onSuccess(ad: InterstitialAd) {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        action()
                    }
                }
                ad.show(activity)
            }

            override fun onFailed(ex: Exception) {
                action()
            }
        })
    } else {
        action()
    }

}