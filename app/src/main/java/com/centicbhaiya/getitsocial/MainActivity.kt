package com.centicbhaiya.getitsocial

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.centicbhaiya.getitsocial.ui.components.BottomNavigationBar
import com.centicbhaiya.getitsocial.ui.components.NavBarItem
import com.centicbhaiya.getitsocial.ui.screens.TabsScreen
import com.centicbhaiya.getitsocial.ui.state.DownloadState
import com.centicbhaiya.getitsocial.ui.state.FbVideoDataState
import com.centicbhaiya.getitsocial.ui.state.TabsScreenState
import com.centicbhaiya.getitsocial.ui.state.newTab
import com.centicbhaiya.getitsocial.ui.theme.AppTheme
import com.centicbhaiya.getitsocial.util.createFileName
import com.centicbhaiya.getitsocial.util.download
import online.desidev.onestate.rememberOneState
import online.desidev.onestate.stateManager

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
        
        val extraTextUrl = intent.extras?.getString(Intent.EXTRA_TEXT)
        Log.d(TAG, "onCreate: intent extra text: $extraTextUrl")


        setContent {
            AppTheme {
                Column {
                    val tabsScreenState = stateManager.rememberOneState(TabsScreenState::class)
                    val appname = stringResource(id = R.string.app_name)


                    SideEffect {
                        tabsScreenState.newTab(extraTextUrl)
                    }

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




