package com.centicbhaiya.getitsocial.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.FullScreenContentCallback


suspend fun loadAppOpenAd(activity: Activity, action: () -> Unit) {
    val ad = AppOpenAdLoader.takeAndLoad()
    if (ad != null) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                action()
            }
        }
        ad.show(activity)
    } else {
        action()
    }
}


suspend fun checkAndShowAd(activity: Context, action: () -> Unit) {
    if (!AdClickCounter.check()) {
        println("cannot show ad at this time..")
        action()
        return
    }

    println("takeAndLoad()")
    val ad = InterstitialAdLoader.takeAndLoad()
    if (ad == null) {
        println("could not load ad")
        action()
    } else {
        println("ad loaded")
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                action()
            }
        }
        ad.show(activity as Activity)
    }
}