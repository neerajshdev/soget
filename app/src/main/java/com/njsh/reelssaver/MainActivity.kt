package com.njsh.reelssaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.njsh.reelssaver.layer.ui.UiState
import com.njsh.reelssaver.layer.ui.pages.PageHost

private const val TAG = "MainActivity.kt"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState = remember { UiState()}
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




