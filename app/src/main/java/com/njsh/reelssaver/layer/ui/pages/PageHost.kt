package com.njsh.reelssaver.layer.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.njsh.reelssaver.layer.ui.UiState
import com.njsh.reelssaver.layer.ui.components.Splash

object RouteName {
    val HOME = "HOME"
    val INSTAGRAM = "INSTAGRAM"
    val FACEBOOK = "FACEBOOK"
    val SHORT_VIDEOS = "SHORT VIDEOS"
}

@Composable
fun PageHost(modifier: Modifier = Modifier, uiState: UiState) {
    val navController = rememberNavController()
    var splash by rememberSaveable { mutableStateOf(true) }

    if (splash) {
        Splash(modifier = modifier, uiState) { splash = false }
    } else {
        NavHost(
            navController = navController, startDestination = RouteName.HOME, modifier = modifier
        ) {
            composable(RouteName.HOME) {
                Home(navController)
            }
            composable(RouteName.INSTAGRAM) {
                InstagramSaverPage(uiState)
            }
            composable(RouteName.FACEBOOK) {
                FbSaverPager(uiState)
            }
            composable(RouteName.SHORT_VIDEOS) {}
        }
    }
}
