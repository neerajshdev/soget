package com.njsh.instadl.ui.pages

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
import com.njsh.instadl.*
import com.njsh.instadl.ads.InterstitialAdLoader
import com.njsh.instadl.ads.NativeAdLoader
import com.njsh.instadl.ads.loadAppOpenAd
import com.njsh.instadl.navigation.Page
import com.njsh.instadl.util.fetchAndActivate
import com.njsh.instadl.util.getUserCountry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import unified.vpn.sdk.Vpn

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

                withContext(Dispatchers.IO) {
                    if (Firebase.remoteConfig.getBoolean(FirebaseKeys.IS_USING_VPN)) {
                        if (!VpnManager.isInitialize()) {
                            try {
                                val vpnConfig = Gson().fromJson(
                                    Firebase.remoteConfig.getString(FirebaseKeys.VPN_CONFIG),
                                    VpnConfig::class.java
                                )
                                val userLocation = getUserCountry(activity)
                                VpnManager.init(
                                    vpnConfig.getTargetLocation(userLocation!!),
                                    vpnConfig.host!!,
                                    vpnConfig.carrierId!!
                                )
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                App.instance().logEventVpnFailedToConnect()
                                return@withContext
                            }
                        }


                        if (!VpnManager.isConnected()) {
                            val isConnected = VpnManager.connect()
                            if (!isConnected) {
                                App.instance().logEventVpnFailedToConnect()
                            }
                        }
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