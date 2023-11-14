package com.centicbhaiya.getitsocial.ads

import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.centicbhaiya.getitsocial.App
import com.centicbhaiya.getitsocial.FirebaseKeys
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object NativeAdLoader {
    val TAG = NativeAdLoader::class.simpleName
    private val adUnitId: String by lazy {
        Firebase.remoteConfig.getString(FirebaseKeys.NATIVE_AD_UNIT_ID)
    }

    private val loadedAds = mutableListOf<NativeAd>()

    private suspend fun load() {
        val deferred = CompletableDeferred<NativeAd?>()

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val options = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        val adLoader: AdLoader =
            AdLoader.Builder(App.instance(), adUnitId).forNativeAd { nativeAd: NativeAd ->
                Log.d(TAG, "completing deferred with ad: $nativeAd")
                deferred.complete(nativeAd)
            }.withNativeAdOptions(options).withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    println("$TAG native ad loaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    deferred.complete(null)
                    println("$TAG could not load ad: $loadAdError")
                }
            }).build()

        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)

        deferred.await()?.let {
            loadedAds.add(it)
            println("$TAG loaded ads: ${loadedAds.size}")
        }
    }

    private suspend fun take(): NativeAd? {
        if (loadedAds.isNotEmpty()) {
            return loadedAds.removeFirst()
        } else {
            load()
            if (loadedAds.isNotEmpty()) return loadedAds.removeFirst()
        }
        return null
    }

    suspend fun takeAndLoad(): NativeAd? = coroutineScope {
        val nativeAd = take()
        CoroutineScope(coroutineContext).launch { load() }
        nativeAd
    }
}