package com.njsh.reelssaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.njsh.reelssaver.ads.AppOpenAdManager
import com.njsh.reelssaver.ads.InterstitialAdManager
import com.njsh.reelssaver.ui.UiState
import com.njsh.reelssaver.ui.screens.AppScreenHost
import com.njsh.reelssaver.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uiState = UiState()
        var keepSplashScreen = true

         installSplashScreen().apply {
             setKeepOnScreenCondition {
                 keepSplashScreen
             }
         }

        uiState.syncFirebase {
            keepSplashScreen = false
        }

        setContent {
            AppTheme {
                AppScreenHost()
            }
        }

        uiState.initState()
        AppOpenAdManager(this)
        InterstitialAdManager.init(this)
    }
}




