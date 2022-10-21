package com.njsh.instadl.ads

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.instadl.App
import com.njsh.instadl.FirebaseKeys
import com.njsh.instadl.api.CallResult

object AppOpenAdLoader
{
    private val adUnitId = Firebase.remoteConfig.getString(FirebaseKeys.APP_OPEN_AD_UNIT_ID)
    private val loadedAds = mutableListOf<AppOpenAd>()

    fun load(callback: ((CallResult<AppOpenAd>) -> Unit)? = null)
    {
        val request = AdRequest.Builder().build()
        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback()
        {
            override fun onAdLoaded(p0: AppOpenAd)
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
        AppOpenAd.load(App.instance(), adUnitId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback)
    }

    fun take(callback: (CallResult<AppOpenAd>) -> Unit)
    {
        if (loadedAds.size > 0)
        {
            callback(CallResult.Success(loadedAds.removeFirst()))
        } else
        {
            load(callback)
        }
    }

    fun takeAndLoad(callback: (CallResult<AppOpenAd>) -> Unit) {
        take(callback)
        load()
    }
}