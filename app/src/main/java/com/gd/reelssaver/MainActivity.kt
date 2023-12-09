package com.gd.reelssaver

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.retainedComponent
import com.arkivanov.decompose.router.stack.childStack
import com.gd.reelssaver.ui.components.BottomNavigationBar
import com.gd.reelssaver.ui.components.NavBarItem
import com.gd.reelssaver.ui.navigation.RootComponent
import com.gd.reelssaver.ui.screens.HomeScreenContent
import com.gd.reelssaver.ui.screens.SplashScreenContent
import com.gd.reelssaver.ui.screens.WebScreenContent
import com.gd.reelssaver.ui.state.FbVideoDataState
import com.gd.reelssaver.ui.state.TabsScreenState
import com.gd.reelssaver.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import online.desidev.onestate.rememberOneState
import online.desidev.onestate.stateManager

class MainActivity : ComponentActivity() {
    companion object {
        val TAG = MainActivity::class.simpleName
    }

    val extraUrl: MutableStateFlow<String?> = MutableStateFlow(null)

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        extraUrl.value = intent?.extras?.getString(Intent.EXTRA_TEXT)
    }

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        Thread.UncaughtExceptionHandler { t, e ->
            e.printStackTrace()
        }

        stateManager.configure {
            stateFactory(FbVideoDataState::class) {
                FbVideoDataState(emptyList())
            }
            stateFactory(TabsScreenState::class) { TabsScreenState() }
        }

        val extraTextUrl = intent.extras?.getString(Intent.EXTRA_TEXT)

        val root = retainedComponent { RootComponent(it) }

        setContent {
            RootContent(root = root)
            /* AppTheme {
                 Column {
                     val tabsScreenState = stateManager.rememberOneState(TabsScreenState::class)
                     val appname = stringResource(id = R.string.app_name)
                     val navController = rememberNavController()
                     var showExitPrompt by remember { mutableStateOf(false) }
 
                     var storagePermission by remember {
                         mutableStateOf(
                             checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                                     Build.VERSION.SDK_INT > 28
                         )
                     }
 
                     val permissionLauncher = rememberLauncherForActivityResult(
                         contract = ActivityResultContracts.RequestPermission(),
                         onResult = {
                             storagePermission = it
                         }
                     )
 
 
                     LaunchedEffect(Unit) {
                         extraUrl.collect { url ->
                             if (url != null) {
                                 tabsScreenState.newTab(url)
                             }
                         }
                     }
 
                     NavHost(navController = navController, startDestination = "splash") {
                         composable("splash") {
                             SplashScreen(navController)
                         }
 
                         composable("tabScreen") {
                             SideEffect {
                                 extraTextUrl?.let {
                                     tabsScreenState.newTab(it)
                                 }
                             }
 
                             TabsScreen(
                                 tabsScreenState = tabsScreenState,
                                 onDownloadVideo = { video ->
                                     if (storagePermission) {
                                         download(
                                             createFileName(appname),
                                             video.videoUrl,
                                             description = "Video downloaded by $appname"
                                         )
                                         App.toast("Video has been added to download!")
                                     } else {
                                         permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                     }
                                 })
                         }
                     }
 
                     if (showExitPrompt) {
                         val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                         ModalBottomSheet(
                             onDismissRequest = { showExitPrompt = false },
                             sheetState = sheetState,
                         ) {
                             ExitPrompt(
                                 onExitCancel = { showExitPrompt = false },
                                 onExitConfirm = { this@MainActivity.finishAfterTransition() }
                             )
                         }
 
                         LaunchedEffect(Unit) {
                             sheetState.expand()
                         }
                     }
 
 
                     BackHandler(showExitPrompt.not()) {
                         showExitPrompt = true
                     }
                 }
             }*/
        }
    }
}


@Composable
fun RootContent(root: RootComponent) {
    Children(stack = root.child) {
        when (val child = it.instance) {
            is RootComponent.Child.HomeScreenChild -> HomeScreenContent(component = child.component)
            is RootComponent.Child.WebScreenChild -> WebScreenContent(component = child.component)
            is RootComponent.Child.SplashScreen -> SplashScreenContent(component = child.component)
        }
    }
}




