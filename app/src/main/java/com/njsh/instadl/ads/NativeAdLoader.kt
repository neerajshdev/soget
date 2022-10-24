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
    val adUnitId by lazy { Firebase.remoteConfig.getString(FirebaseKeys.NATIVE_AD_UNIT_ID) }
    val loadedAds = mutableListOf<NativeAd>()

    suspend fun load() {
        val deferred = CompletableDeferred<NativeAd?>()

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val options = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        val adLoader: AdLoader =
            AdLoader.Builder(App.instance(), adUnitId).forNativeAd { nativeAd: NativeAd ->
                deferred.complete(nativeAd)/*  val style: NativeTemplateStyle =
                          Builder().withMainBackgroundColor(ColorDrawable(-0x1)).build()
                      nativeAdView.setStyles(style)
                      nativeAdView.setNativeAd(nativeAd)
                      nativeAdView.setVisibility(View.VISIBLE)
                      nativeAdView.setAlpha(0f)
                      nativeAdView.animate().alpha(1f)
                      if (_onAttach != null) _onAttach.run()*/
            }.withNativeAdOptions(options).withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    deferred.complete(null)
                }
            }).build()

        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)

        deferred.await()?.let { loadedAds.add(it) }
    }

    private suspend fun take(): NativeAd? {
        if (loadedAds.isNotEmpty()) {
            loadedAds.removeFirst()
        } else {
            load()
            if (loadedAds.isNotEmpty()) return loadedAds.removeFirst()
        }
        return null
    }

    suspend fun takeAndLoad(): NativeAd? = coroutineScope {
        CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
            load()
        }
        take()
    }
}