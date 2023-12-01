package com.gd.reelssaver

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gd.reelssaver.ui.components.BottomNavigationBar
import com.gd.reelssaver.ui.components.ExitPrompt
import com.gd.reelssaver.ui.components.NavBarItem
import com.gd.reelssaver.ui.screens.SplashScreen
import com.gd.reelssaver.ui.screens.TabsScreen
import com.gd.reelssaver.ui.state.FbVideoDataState
import com.gd.reelssaver.ui.state.TabsScreenState
import com.gd.reelssaver.ui.state.newTab
import com.gd.reelssaver.ui.theme.AppTheme
import com.gd.reelssaver.util.createFileName
import com.gd.reelssaver.util.download
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

    @OptIn(ExperimentalMaterial3Api::class)
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


        setContent {
            AppTheme {
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
            }
        }
    }
}


@Composable
fun BottomBar(navController: NavController) {
    BottomNavigationBar(
        NavBarItem(
            name = "Tab",
            iconRes = R.drawable.ic_tabs,
            onItemSelect = { navController.navigate("tabs") }
        ),

        NavBarItem(
            name = "Progress",
            iconRes = R.drawable.round_download_24,
            onItemSelect = {
                navController.navigate("progress"); Log.d(
                MainActivity.TAG,
                "navgate to progress"
            )
            }
        ),

        NavBarItem(
            name = "Player",
            iconRes = R.drawable.round_folder_24,
            onItemSelect = { navController.navigate("player") }
        )
    )
}




