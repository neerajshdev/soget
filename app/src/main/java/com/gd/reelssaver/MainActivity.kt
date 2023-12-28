package com.gd.reelssaver

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.retainedComponent
import com.gd.reelssaver.ui.screens.browser.BrowserContent
import com.gd.reelssaver.ui.screens.browser.DefaultBrowserComponent
import com.gd.reelssaver.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    companion object {
        val TAG = MainActivity::class.simpleName
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }


    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        Thread.UncaughtExceptionHandler { t, e ->
            e.printStackTrace()
        }

        val root = retainedComponent {
            DefaultBrowserComponent(it, false)
        }

//        root.lifecycle.doOnResume {
//            root.extraUrl.value = intent?.extras?.getString(Intent.EXTRA_TEXT)
//            Log.d(TAG, "onNewIntent: extra text: ${root.extraUrl.value}")
//        }

        setContent {
            val isDarktheme by root.isDarkTheme.subscribeAsState()
            AppTheme(useDarkTheme = isDarktheme) {
                BrowserContent(comp = root)
            }
        }
    }
}



