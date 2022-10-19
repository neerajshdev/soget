package com.njsh.instadl.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.njsh.instadl.ui.components.Drawer
import com.njsh.instadl.ui.components.TopAppbar
import com.njsh.instadl.ui.theme.AppTheme
import kotlinx.coroutines.launch


object ActivityContent
{
    private val drawer = Drawer()
    private val topAppbar = TopAppbar()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun Content()
    {
        AppTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
            ) {
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()

                // events handling
                topAppbar.onMenuClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }

                drawer.onClose = {
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                }

                // Page Selection
                drawer.onItemSelect = { item ->

                }

                val welcomePage = PageWelcome() {}
                welcomePage.drawContent()

                val navController = rememberNavController()
                NavHost(
                    navController = navController, startDestination = Route.SplashScreen.name
                ) {
                    composable(route = Route.SplashScreen.name) {
                        val splashScreen = PageSplash { routeName ->
                            navController.navigate(routeName) {
                                popUpTo(Route.SplashScreen.name) {
                                    inclusive = true
                                }
                            }
                        }
                        splashScreen.drawContent()
                    }

                    composable(route = Route.MainScreen.name) {
                        PageMainScreen() { routeName ->
                            navController.navigate(
                                routeName
                            )
                        }.drawContent()
                    }

                    composable(route = Route.WelcomeScreen.name) {
                        PageWelcome { routeName ->
                            navController.navigate(
                                routeName
                            )
                        }.drawContent()
                    }

                    composable(route = Route.InstagramReelScreen.name) { PageInstagram().drawContent() }
                }
            }
        }
    }
}