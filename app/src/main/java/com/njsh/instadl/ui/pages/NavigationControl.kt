package com.njsh.instadl.ui.pages

import com.njsh.instadl.navigation.PageNavigator

sealed class Route(val name: String)
{
    object LoadingScreen : Route("SplashScreen")
    object MainScreen : Route("MainScreen")
    object WelcomeScreen : Route("WelcomeScreen")
    object InstagramReelScreen : Route("InstagramReelScreen")
    object FacebookVideoScreen : Route("FacebookVideoScreen")
}
