package com.njsh.instadl.ui.pages

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.firebase.ktx.Firebase
import com.njsh.instadl.*
import com.njsh.instadl.R
import com.njsh.instadl.ads.AppOpenAdLoader
import com.njsh.instadl.ads.FullScreenContentBackAction
import com.njsh.instadl.ads.InterstitialAdLoader
import com.njsh.instadl.ads.showAd
import com.njsh.instadl.api.CallResult
import com.njsh.instadl.navigation.Page
import com.njsh.instadl.ui.components.CircularProgressBar
import com.njsh.instadl.util.fetchAndActivate
import com.njsh.instadl.util.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PageLoadingContent(private val navController: NavController) : Page()
{
    private val TAG = PageLoadingContent::class.simpleName

    init
    {
        addContent {
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                ShowLoading()
            }

            val activity = LocalContext.current as Activity

            LaunchedEffect(key1 = Unit) {
                Firebase.fetchAndActivate()
                loadAppOpenAd(activity)
            }
        }
    }



    @Composable
    private fun ShowLoading()
    {
        CircularProgressBar(remember { mutableStateOf(true) })
    }

    private fun loadAppOpenAd(activity: Activity)
    {
        AppOpenAdLoader.takeAndLoad { result ->
            if (result is CallResult.Success)
            {
                val ad: AppOpenAd = result.data
                showAd(ad, activity) { action ->
                    if (action is FullScreenContentBackAction.OnDismissed)
                    {
                        navigateToNextScreen()
                    }
                }
            } else if (result is CallResult.Failed)
            {
                Log.d(TAG, "could not load ad ")
                navigateToNextScreen()
            }
        }

        // load in advance
        InterstitialAdLoader.load()
    }

    private fun navigateToNextScreen()
    {
        navController.navigate(Route.WelcomeScreen.name) {
            popUpTo(Route.LoadingScreen.name) {
                inclusive = true
            }
        }
    }
}