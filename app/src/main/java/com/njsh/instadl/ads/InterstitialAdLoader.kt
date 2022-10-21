package com.njsh.instadl.ads

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.instadl.App
import com.njsh.instadl.FirebaseKeys
import com.njsh.instadl.api.CallResult

object InterstitialAdLoader
{
    private val adUnitId = Firebase.remoteConfig.getString(FirebaseKeys.INTERSTITIAL_AD_UNIT_ID)
    private val loadedAds = mutableListOf<InterstitialAd>()

    fun load(callback: ((CallResult<InterstitialAd>) -> Unit)? = null)
    {
        val request = AdRequest.Builder().build()
        val loadCallback = object : InterstitialAdLoadCallback()
        {
            override fun onAdLoaded(p0: InterstitialAd)
            {
                if (callback != null) {
                    callback(CallResult.Success(p0))
                } else {
                    loadedAds.add(p0)
                }
            }

            override fun onAdFailedToLoad(p0: LoadAdError)
            {
                callback?.invoke(CallResult.Failed(p0.message))
            }
        }
        InterstitialAd.load(App.instance(), adUnitId, request, loadCallback)
    }

    fun take(callback: (CallResult<InterstitialAd>) -> Unit)
    {
        if (loadedAds.size > 0)
        {
            callback(CallResult.Success(loadedAds.removeFirst()))
        } else
        {
            load() {
                callback(it)
            }
        }
    }

    fun takeAndLoad(callback: (CallResult<InterstitialAd>) -> Unit) {
        take(callback)
        load()
    }
}