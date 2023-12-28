package com.gd.reelssaver

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.retainedComponent
import com.desidev.downloader.Downloader
import com.gd.reelssaver.ui.screens.DefaultRootComponent
import com.gd.reelssaver.ui.screens.RootContent
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

        val dbDir = filesDir
        val videoDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val root = retainedComponent {
            DefaultRootComponent(it, Downloader(dbDir), videoDir)
        }

//        root.lifecycle.doOnResume {
//            root.extraUrl.value = intent?.extras?.getString(Intent.EXTRA_TEXT)
//            Log.d(TAG, "onNewIntent: extra text: ${root.extraUrl.value}")
//        }

        setContent {
            val isDarktheme by root.isDarkTheme.subscribeAsState()
            AppTheme(useDarkTheme = isDarktheme) {
                RootContent(component = root)
            }
        }
    }
}



