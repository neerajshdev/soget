package com.njsh.reelssaver.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.njsh.reelssaver.*
import com.njsh.reelssaver.ads.InterstitialAdLoader
import com.njsh.reelssaver.ads.NativeAdLoader
import com.njsh.reelssaver.ads.loadAppOpenAd
import com.njsh.reelssaver.navigation.Page
import com.njsh.reelssaver.util.fetchAndActivate
import com.njsh.reelssaver.util.getUserCountry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PageLoadingScreen(private val navController: NavController) : Page() {

    init {
        addContent {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )

            val activity = LocalContext.current as MainActivity
            LaunchedEffect(key1 = Unit) {
                if (!AppPref.pref.getBoolean(AppPref.FIREBASE_FETCHED, false)) {
                    Firebase.fetchAndActivate()
                    if (App.debug) {
                        println("Firebase call to fetch complete")
                    }
                }

                NativeAdLoader.load()
                InterstitialAdLoader.load()

                loadAppOpenAd(activity) {
                    moveToWelcomeScreen()
                }
            }
        }
    }

    private fun moveToWelcomeScreen() {
        if (!navController.popBackStack()) {
            navController.navigate(Route.WelcomeScreen.name) {
                popUpTo(Route.LoadingScreen.name) {
                    inclusive = true
                }
            }
        }
    }
}