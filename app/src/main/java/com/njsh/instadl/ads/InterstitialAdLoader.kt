package com.njsh.instadl.ads

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.instadl.App
import com.njsh.instadl.FirebaseKeys
import com.njsh.instadl.util.CallResult

object InterstitialAdLoader {
    private val adUnitId = Firebase.remoteConfig.getString(FirebaseKeys.INTERSTITIAL_AD_UNIT_ID)
    private val loadedAds = mutableListOf<InterstitialAd>()

    fun load(result: CallResult<InterstitialAd>? = null) {
        val request = AdRequest.Builder().build()
        val callback = object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                if (result == null) {
                    loadedAds.add(ad)
                } else {
                    result.onSuccess(ad)
                }
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                result?.onFailed(java.io.IOException(p0.message))
            }
        }

        InterstitialAd.load(App.instance(), adUnitId, request, callback)
    }

    fun take(result: CallResult<InterstitialAd>) {
        if (loadedAds.size > 0) {
            result.onSuccess(loadedAds.removeFirst())
        } else {
            load(result)
        }
    }

    fun takeAndLoad(result: CallResult<InterstitialAd>) {
        take(result)
        load()
    }
}