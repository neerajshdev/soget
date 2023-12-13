package com.gd.reelssaver.ads

import android.util.Log
import com.gd.reelssaver.FirebaseKeys
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NativeAdLoader {
    val TAG = NativeAdLoader::class.simpleName
    private val adUnitId: String by lazy {
        Firebase.remoteConfig.getString(FirebaseKeys.NATIVE_AD_UNIT_ID).also {
            Log.d(TAG, "ad Unit Id: $it")
        }.trim()
        "ca-app-pub-3940256099942544/2247696110"
    }

    private val loadedAds = mutableListOf<NativeAd>()

    private suspend fun load(): NativeAd? {
        return suspendCoroutine { cont ->
            val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
            val options = NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_BOTTOM_RIGHT)
                .setVideoOptions(videoOptions)
                .build()

            val adLoader: AdLoader =
                AdLoader.Builder(com.gd.reelssaver.App.instance(), adUnitId)
                    .forNativeAd { nativeAd: NativeAd ->
                        cont.resume(nativeAd)
                    }.withNativeAdOptions(options).withAdListener(object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d(TAG, "onAdLoaded, loaded ads: ${loadedAds.size}")
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            cont.resume(null)
                            Log.d(TAG, "onAdFailedToLoad: $loadAdError")
                        }
                    })
                    .build()

            val adRequest = AdRequest.Builder().build()
            adLoader.loadAd(adRequest)
        }
    }

    private suspend fun take(): NativeAd? {
        if (loadedAds.isNotEmpty()) {
            return loadedAds.removeFirst()
        }
        return load()
    }

    suspend fun takeAndLoad(): NativeAd? = coroutineScope {

        val nativeAd = take()
        CoroutineScope(coroutineContext).launch {
            load()?.let { loadedAds.add(it) }
        }
        nativeAd
    }
}