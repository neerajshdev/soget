package com.njsh.reelssaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.njsh.reelssaver.ads.loadAppOpenAd
import com.njsh.reelssaver.layer.ui.UiState
import com.njsh.reelssaver.layer.ui.pages.PageHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MainActivity.kt"

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
            PageHost(modifier = Modifier.fillMaxSize(), uiState)
            DisposableEffect(key1 = uiState, effect = {
                uiState.initState()
                onDispose {
                    uiState.clearState()
                }
            })
        }
    }
}




