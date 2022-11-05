package com.njsh.reelssaver.ui.pages

sealed class Route(val name: String) {
    object LoadingScreen : Route("SplashScreen")
    object MainScreen : Route("MainScreen")
    object WelcomeScreen : Route("WelcomeScreen")
    object InstagramReelScreen : Route("InstagramReelScreen")
    object FacebookVideoScreen : Route("FacebookVideoScreen")
    object ExitDialog : Route("ExitDialog")
}
