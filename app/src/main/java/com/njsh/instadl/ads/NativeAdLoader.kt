package com.njsh.instadl.ads

import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.instadl.App
import com.njsh.instadl.FirebaseKeys
import kotlinx.coroutines.*

object NativeAdLoader {
    const val TAG = "NativeAdLoader"
    val adUnitId by lazy { Firebase.remoteConfig.getString(FirebaseKeys.NATIVE_AD_UNIT_ID) }
    val loadedAds = mutableListOf<NativeAd>()

    suspend fun load() {
        val deferred = CompletableDeferred<NativeAd?>()

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val options = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        val adLoader: AdLoader =
            AdLoader.Builder(App.instance(), adUnitId).forNativeAd { nativeAd: NativeAd ->
                println("$TAG completing deferred with ad: $nativeAd")
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
        println("$TAG loaded ads: ${loadedAds.size}")
        val nativeAd = take()
        CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
            load()
        }
        nativeAd
    }
}