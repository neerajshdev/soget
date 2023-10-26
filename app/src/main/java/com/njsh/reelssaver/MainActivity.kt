package com.njsh.reelssaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.njsh.reelssaver.layer.ui.UiState
import com.njsh.reelssaver.layer.ui.pages.PageHost

class MainActivity : ComponentActivity() {
    companion object {
        val TAG = MainActivity::class.simpleName
    }
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




