package com.gd.reelssaver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.retainedComponent
import com.arkivanov.essenty.lifecycle.doOnResume
import com.gd.reelssaver.ui.blocs.ExitPromptComponent
import com.gd.reelssaver.ui.blocs.RootComponent
import com.gd.reelssaver.ui.blocs.TabChooserComponent
import com.gd.reelssaver.ui.contents.ExitPromptContent
import com.gd.reelssaver.ui.contents.HomeScreenContent
import com.gd.reelssaver.ui.contents.SplashScreenContent
import com.gd.reelssaver.ui.contents.TabChooserContent
import com.gd.reelssaver.ui.contents.WebScreenContent
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

        Thread.UncaughtExceptionHandler { t, e ->
            e.printStackTrace()
        }

        val root = retainedComponent {
            RootComponent(it, onAppClose = {
                finish()
            })
        }

        root.lifecycle.doOnResume {
            root.extraUrl.value = intent?.extras?.getString(Intent.EXTRA_TEXT)
            Log.d(TAG, "onNewIntent: extra text: ${root.extraUrl.value}")
        }

        setContent {
            RootContent(root = root)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootContent(root: RootComponent) {
    val extraUrl by root.extraUrl.collectAsState()
    LaunchedEffect(key1 = extraUrl) {
        extraUrl?.let {
            val url = findFirstUrl(it)
            if (url != null) {
                root.onEvent(RootComponent.Event.OpenNewTabWithExtraText(url))
            }
        }
    }

    AppTheme(useDarkTheme = root.useDarkTheme.collectAsState().value) {
        /* Children(
             stack = root.child,
             animation = stackAnimation(fade() + scale())
         ) {
             when (val child = it.instance) {
                 is RootComponent.Child.HomeScreenChild -> HomeScreenContent(component = child.component)
                 is RootComponent.Child.WebScreenChild -> WebScreenContent(component = child.component)
                 is RootComponent.Child.SplashScreen -> SplashScreenContent(component = child.component)
             }
         }
 */
        val childGraph by root.child.subscribeAsState()
        Crossfade(targetState = childGraph.active, label = "") {
            when (val child = it.instance) {
                is RootComponent.Child.HomeScreenChild -> HomeScreenContent(component = child.component)
                is RootComponent.Child.WebScreenChild -> WebScreenContent(component = child.component)
                is RootComponent.Child.SplashScreen -> SplashScreenContent(component = child.component)
            }
        }

        val sheet by root.bottomSheet.subscribeAsState()
        val childComp = sheet.child?.instance

        if (childComp != null) {
            val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            ModalBottomSheet(
                onDismissRequest = { root.onEvent(RootComponent.Event.DismissBottomSheet) },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                sheetState = bottomSheetState
            ) {
                when (childComp) {
                    is TabChooserComponent -> TabChooserContent(component = childComp)
                    is ExitPromptComponent -> ExitPromptContent(component = childComp)
                }
            }

            LaunchedEffect(key1 = bottomSheetState) {
                bottomSheetState.show()
            }
        }

        BackHandler {
            root.onEvent(RootComponent.Event.ShowExitPrompt)
        }
    }
}




