package com.njsh.reelssaver.layer.ui.pages

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.njsh.reelssaver.ads.loadAppOpenAd
import com.njsh.reelssaver.layer.ui.UiState
import com.njsh.reelssaver.layer.ui.theme.AppTheme
import kotlinx.coroutines.launch

object RouteName {
    val HOME = "HOME"
    val INSTAGRAM = "INSTAGRAM"
    val FACEBOOK = "FACEBOOK"
    val SHORT_VIDEOS = "SHORT VIDEOS"
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PageHost(modifier: Modifier = Modifier, uiState: UiState) {
    val scope = rememberCoroutineScope()
    val navController = rememberAnimatedNavController()
    val context = LocalContext.current

    DisposableEffect(uiState) {
        val observer = LifecycleEventObserver { source, event ->
            when(event) {
                Lifecycle.Event.ON_START -> {
//                    println("On_start event! compose side")
                    scope.launch {
//                        println("loading app open ad")
                        loadAppOpenAd(context as Activity) {}
                    }
                }
                else -> {}
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
        onDispose {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(observer)
        }
    }

    AppTheme {
        AnimatedNavHost(
            navController = navController,
            startDestination = RouteName.HOME,
            modifier = modifier
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
                                context.window,
                                context.window.decorView
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
