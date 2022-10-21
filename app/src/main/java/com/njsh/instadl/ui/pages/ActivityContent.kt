package com.njsh.instadl.ui.pages

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.njsh.instadl.App
import com.njsh.instadl.R
import com.njsh.instadl.ViewModel
import com.njsh.instadl.ui.theme.AppTheme
import kotlinx.coroutines.delay


object ActivityContent
{
    private val isSplashShown = mutableStateOf(false)

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun Content()
    {
        AppTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
            ) {
                if (!isSplashShown.value) {
                    ShowSplash()
                    LaunchedEffect(key1 = Unit ) {
                        delay(4000)
                        isSplashShown.value = true
                    }
                    return@Surface
                }

                val navController = rememberNavController()

                if (ViewModel.isUserOnline.value) {
                    NavHost(
                        navController = navController, startDestination = Route.LoadingScreen.name
                    ) {
                        composable(route = Route.LoadingScreen.name) {
                            val loading = PageLoadingContent(navController)
                            loading.drawContent()
                        }

                        composable(route = Route.MainScreen.name) {
                            PageMainScreen { routeName ->
                                navController.navigate(routeName)
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
                        composable(route = Route.FacebookVideoScreen.name) { PageFacebookVideo().drawContent() }
                    }
                } else {
                    PageUserOfflineScreen().drawContent()
                }

                LaunchedEffect(key1 = Unit ) {
                    val connectivityManager =
                        App.instance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                    val netReq = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build()

                    connectivityManager.registerNetworkCallback(
                        netReq,
                        object : ConnectivityManager.NetworkCallback()
                        {
                            override fun onAvailable(network: Network)
                            {
                                ViewModel.isUserOnline.value = true
                            }

                            override fun onLost(network: Network)
                            {
                                ViewModel.isUserOnline.value = false
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun ShowSplash()
    {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.splash_image),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center)
            )

            LaunchedEffect(key1 = Unit) {
                delay(4000)
                isSplashShown.value = true
            }
        }
    }
}