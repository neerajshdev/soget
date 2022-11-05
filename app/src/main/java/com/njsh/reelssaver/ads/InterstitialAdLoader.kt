package com.njsh.reelssaver.ads

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.reelssaver.App
import com.njsh.reelssaver.FirebaseKeys
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object InterstitialAdLoader {
    private val adUnitId: String
        get() = Firebase.remoteConfig.getString(FirebaseKeys.INTERSTITIAL_AD_UNIT_ID)

    private val loadedAds = mutableListOf<InterstitialAd>()

    suspend fun load() {
        val interAdLoading = CompletableDeferred<InterstitialAd?>()

        val request = AdRequest.Builder().build()

        val callback = object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interAdLoading.complete(ad)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                interAdLoading.complete(null)
            }
        }

        InterstitialAd.load(App.instance(), adUnitId, request, callback)
        interAdLoading.await()?.let {
            loadedAds.add(it)
            println("There are ${loadedAds.size} Interstitial ads in Interstitial ad loader")
        }
    }

    suspend fun take(): InterstitialAd? {
        return if (loadedAds.size > 0) {
            loadedAds.removeFirst()
        } else {
            load()
            loadedAds.removeFirstOrNull()
        }
    }

    suspend fun takeAndLoad() = coroutineScope {
        CoroutineScope(coroutineContext).launch { load() }
        take()
    }
}