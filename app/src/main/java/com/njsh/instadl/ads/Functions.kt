package com.njsh.instadl.ads

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.njsh.instadl.api.CallResult

fun showAd(ad: AppOpenAd, activity: Activity, backAction: (FullScreenContentBackAction)->Unit) {
    val callback = object : FullScreenContentCallback() {
        override fun onAdClicked()
        {
            backAction(FullScreenContentBackAction.OnAdClicked)
        }

        override fun onAdDismissedFullScreenContent()
        {
            backAction(FullScreenContentBackAction.OnDismissed)
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError)
        {
            backAction(FullScreenContentBackAction.OnAdFailedToShow)
        }

        override fun onAdShowedFullScreenContent()
        {
            backAction(FullScreenContentBackAction.OnAdShowed)
        }

        override fun onAdImpression()
        {
            backAction(FullScreenContentBackAction.OnAdImpression)
        }
    }

    ad.fullScreenContentCallback = callback
    ad.show(activity)
}


fun showAd(ad: InterstitialAd, activity: Activity, backAction: (FullScreenContentBackAction)->Unit) {
    val callback = object : FullScreenContentCallback() {
        override fun onAdClicked()
        {
            backAction(FullScreenContentBackAction.OnAdClicked)
        }

        override fun onAdDismissedFullScreenContent()
        {
            backAction(FullScreenContentBackAction.OnDismissed)
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError)
        {
            backAction(FullScreenContentBackAction.OnAdFailedToShow)
        }

        override fun onAdShowedFullScreenContent()
        {
            backAction(FullScreenContentBackAction.OnAdShowed)
        }

        override fun onAdImpression()
        {
            backAction(FullScreenContentBackAction.OnAdImpression)
        }
    }

    ad.fullScreenContentCallback = callback
    ad.show(activity)
}

/**
 * Show the interstitial ad if failed, then provided action will be executed
 */
fun checkAndShowAd(activity: Activity, action : ()-> Unit) {
    if (AdClickCounter.check()) {
        InterstitialAdLoader.takeAndLoad { callResult ->
            if (callResult is CallResult.Success) {
                showAd(callResult.data, activity) {}
            } else {
                action()
            }
        }
    } else {
        action()
    }
}