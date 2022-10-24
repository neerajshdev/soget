package com.njsh.instadl.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.ktx.Firebase
import com.njsh.instadl.App
import com.njsh.instadl.AppPref
import com.njsh.instadl.MainActivity
import com.njsh.instadl.VpnManager
import com.njsh.instadl.ads.InterstitialAdLoader
import com.njsh.instadl.ads.loadAppOpenAd
import com.njsh.instadl.navigation.Page
import com.njsh.instadl.util.fetchAndActivate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PageLoadingScreen(private val navController: NavController) : Page() {

    init {
        addContent {
            CircularProgressIndicator(modifier = Modifier
                .fillMaxSize()
                .wrapContentSize())

            val activity = LocalContext.current as MainActivity
            LaunchedEffect(key1 = Unit) {
                if (!AppPref.pref.getBoolean(AppPref.FIREBASE_FETCHED, false)) {
                    Firebase.fetchAndActivate()
                    // load ad in advance
                    InterstitialAdLoader.load()
                    if (App.debug) {
                        println("Firebase call to fetch complete")
                    }
                }

                withContext(Dispatchers.IO) {
                    if (!VpnManager.isInitialize()) {
                        VpnManager.init(
                            "us",
                            "https://d2isj403unfbyl.cloudfront.net",
                            "ubi_anlubi"
                        )
                    }

                    if (!VpnManager.isConnected()) {
                        VpnManager.connect()
                    }
                }

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