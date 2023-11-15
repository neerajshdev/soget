package com.centicbhaiya.getitsocial

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.centicbhaiya.getitsocial.ui.components.BottomNavigationBar
import com.centicbhaiya.getitsocial.ui.components.NavBarItem
import com.centicbhaiya.getitsocial.ui.screens.DownloadsScreen
import com.centicbhaiya.getitsocial.ui.screens.TabsScreen
import com.centicbhaiya.getitsocial.ui.state.DownloadState
import com.centicbhaiya.getitsocial.ui.state.FbVideoDataState
import com.centicbhaiya.getitsocial.ui.state.TabsScreenState
import com.centicbhaiya.getitsocial.ui.theme.AppTheme
import com.centicbhaiya.getitsocial.util.createFileName
import com.centicbhaiya.getitsocial.util.download
import online.desidev.onestate.rememberOneState
import online.desidev.onestate.stateManager
import online.desidev.onestate.toState

class MainActivity : ComponentActivity() {
    companion object {
        val TAG = MainActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)


        stateManager.configure {
            stateFactory(FbVideoDataState::class) {
                FbVideoDataState(emptyList())
            }

            stateFactory(TabsScreenState::class) { TabsScreenState() }
            stateFactory(DownloadState::class) { DownloadState() }
        }

        val downloadState = stateManager.getState(DownloadState::class)
        val fetchDownloader = FetchDownloader(this, downloadState)


        setContent {
            AppTheme {
                Column {
                    val tabsScreenState = stateManager.rememberOneState(TabsScreenState::class)
//                    val navController = rememberNavController()
//                    val downloads by downloadState.toState { it.getDownloads() }

//                    NavHost(
//                        navController = navController,
//                        startDestination = "tabs",
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        composable("tabs") {
//                            TabsScreen(
//                                modifier = Modifier.fillMaxSize(),
//                                tabsScreenState,
//                                fetchDownloader
//                            )
//                        }
//
//                        composable("progress") {
//                            DownloadsScreen(downloads = downloads)
//                        }
//
////                        composable("player") {
////                            Text(text = "Player")
////                        }
//                    }

//                    BottomBar(navController = navController)

                    val appname = stringResource(id = R.string.app_name)
                    TabsScreen(tabsScreenState = tabsScreenState, onDownloadVideo = { video ->
                        download(
                            createFileName(appname),
                            video.videoUrl,
                            description = "Video downloaded by $appname"
                        )

                        App.toast("Video has been added to download!")
                    })
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




