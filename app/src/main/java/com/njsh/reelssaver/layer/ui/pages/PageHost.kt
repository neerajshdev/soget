package com.njsh.reelssaver.layer.ui.pages

import android.app.Activity
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.njsh.reelssaver.layer.ui.UiState
import com.njsh.reelssaver.layer.ui.theme.AppTheme

object RouteName {
    val HOME = "HOME"
    val INSTAGRAM = "INSTAGRAM"
    val FACEBOOK = "FACEBOOK"
    val SHORT_VIDEOS = "SHORT VIDEOS"
}

@Composable
fun PageHost(modifier: Modifier = Modifier, uiState: UiState) {
    val navController = rememberNavController()

    AppTheme {
        NavHost(
            navController = navController, startDestination = RouteName.HOME, modifier = modifier
        ) {
            composable(RouteName.HOME, enterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            }, exitTransition = {
                slideOutHorizontally { -it }
            }) {
                Home(navController)
            }

            composable(RouteName.INSTAGRAM, enterTransition = {
                slideInHorizontally { it }
            }, popExitTransition = {
                slideOutHorizontally { it }
            }) {
                InstagramSaverPage(uiState)
            }


            composable(RouteName.FACEBOOK,
                enterTransition = { slideInHorizontally { it } },
                popExitTransition = { slideOutHorizontally { it } }) {
                FbSaverPager(uiState)
            }

            composable(RouteName.SHORT_VIDEOS,
                enterTransition = { slideInHorizontally { it } },
                popExitTransition = { slideOutHorizontally { it } }) {
                val context = LocalContext.current
                ShortVideoPage(shortVideoState = uiState.shortVideoState)

                if (context is Activity) {
                    DisposableEffect(uiState.shortVideoState) {
                        WindowCompat.setDecorFitsSystemWindows(context.window, false)
                        WindowCompat.getInsetsController(context.window, context.window.decorView)
                            .apply {
                                isAppearanceLightNavigationBars = false
                                isAppearanceLightStatusBars = false
                            }
                        onDispose {
                            WindowCompat.setDecorFitsSystemWindows(context.window, true)
                            WindowCompat.getInsetsController(
                                context.window, context.window.decorView
                            ).apply {
                                isAppearanceLightNavigationBars = true
                                isAppearanceLightStatusBars = true
                            }
                        }
                    }
                }
            }
        }
    }
}
