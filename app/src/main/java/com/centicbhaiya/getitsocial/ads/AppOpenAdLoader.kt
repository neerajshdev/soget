package com.centicbhaiya.getitsocial.ads

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.centicbhaiya.getitsocial.App
import com.centicbhaiya.getitsocial.FirebaseKeys
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object AppOpenAdLoader {
    private val loadedAds = mutableListOf<AppOpenAd>()

    private fun adUnitId() = Firebase.remoteConfig.getString(FirebaseKeys.APP_OPEN_AD_UNIT_ID)

    private suspend fun load() {
        val deferred = CompletableDeferred<AppOpenAd?>()
        val request = AdRequest.Builder().build()

        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) { deferred.complete(ad) }
            override fun onAdFailedToLoad(p0: LoadAdError) {deferred.complete(null)}
        }

        AppOpenAd.load(
            App.instance(),
            adUnitId(),
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            loadCallback
        )

        deferred.await()?.let {
            loadedAds.add(it)
        }
    }

    suspend fun take(): AppOpenAd? {
        if (loadedAds.size > 0) {
            return loadedAds.removeFirst()
        } else {
            load()
            return loadedAds.removeFirstOrNull()
        }
    }

    suspend fun takeAndLoad() = coroutineScope {
        CoroutineScope(coroutineContext).launch { load() }
        take()
    }
}