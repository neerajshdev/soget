package com.njsh.reelssaver.ui.screens

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.njsh.reelssaver.ui.theme.AppTheme

object RouteName {
    const val VideoDownloader = "video_downloader"
    const val HowToScreen = "how_to_screen"
}


@Composable
fun AppScreenHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    AppTheme {
        Surface {
            NavHost(
                navController = navController,
                startDestination = RouteName.VideoDownloader,
                modifier = modifier
            ) {
                composable(
                    route = RouteName.VideoDownloader,
                    enterTransition = {
                        slideInHorizontally(initialOffsetX = { -it })
                    },
                    exitTransition = {
                        slideOutHorizontally { -it }
                    }
                ) {
                    VideoDownloaderScreen(navController)
                }


                composable(route = RouteName.HowToScreen) {
                    HowToUseScreen(navController)
                }
            }
        }
    }
}
