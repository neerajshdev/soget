package com.gd.reelssaver

import android.content.Intent
import android.net.Uri
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
import com.gd.reelssaver.ui.screens.RootComponentCallback
import com.gd.reelssaver.ui.screens.RootContent
import com.gd.reelssaver.ui.screens.browser.TabPage
import com.gd.reelssaver.ui.theme.AppTheme
import com.gd.reelssaver.util.findFirstUrl


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

        Thread.UncaughtExceptionHandler { _, e ->
            e.printStackTrace()
        }

        val dbDir = filesDir
        val videoDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)


        val initialPage = intent.extras?.getString(Intent.EXTRA_TEXT)?.let {
            findFirstUrl(it)?.let { str ->
                TabPage.Webpage(initialUrl = str)
            }
        } ?: TabPage.Homepage

        val root = retainedComponent {
            DefaultRootComponent(
                componentContext =  it,
                downloader = Downloader(dbDir),
                parentVideoDir = videoDir,
                initialPage = initialPage,
                callback = object : RootComponentCallback {
                    override fun onOpenVideoInPlayer(filepath: String) {
                        openVideoInPlayer(filepath)
                    }

                    override fun onExitConfirm() {
                        finishAfterTransition()
                    }
                })
        }

        setContent {
            val isDarktheme by root.isDarkTheme.subscribeAsState()
            AppTheme(useDarkTheme = isDarktheme) {
                RootContent(component = root)
            }
        }
    }

    fun openVideoInPlayer(path: String) {
        val uri = Uri.parse(path)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "video/*")
        startActivity(intent)
    }
}



