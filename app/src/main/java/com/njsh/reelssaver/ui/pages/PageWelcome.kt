package com.njsh.reelssaver.ui.pages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.njsh.reelssaver.BuildConfig
import com.njsh.reelssaver.R
import com.njsh.reelssaver.ads.checkAndShowAd
import com.njsh.reelssaver.navigation.Page
import com.njsh.reelssaver.ui.components.NativeAdView
import com.njsh.reelssaver.ui.components.RoundedButton
import com.njsh.reelssaver.ui.components.TopAppbar
import com.njsh.reelssaver.ui.theme.AppTheme
import kotlinx.coroutines.launch

class PageWelcome(val navController: NavController) : Page() {
    val topAppbar by lazy { TopAppbar("ALL VIDEO DOWNLOADER") }

    init {
        addContent {
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                Content()
            }

            val activity = LocalContext.current
            val scope = rememberCoroutineScope()
            BackHandler {
                scope.launch {
                    checkAndShowAd(activity) {
                        navController.navigate(Route.ExitDialog.name)
                    }
                }
            }
        }
    }


    @Composable
    private fun Content() {
        Column(
            Modifier.fillMaxSize()
        ) {
            topAppbar.drawContent()
            NativeAdView(modifier = Modifier.padding(16.dp))
            OptionsLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(1f)
            )
        }
    }


    @Composable
    private fun OptionsLayout(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        Box(contentAlignment = Alignment.Center, modifier = modifier) {
            Column {
                RoundedButton(painter = painterResource(id = R.drawable.ic_right_to_bracket_solid),
                    label = "ENTER",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        scope.launch {
                            checkAndShowAd(context) { navController.navigate(Route.MainScreen.name) }
                        }
                    })

                RoundedButton(painter = painterResource(id = R.drawable.ic_share_nodes_solid),
                    label = "SHARE THIS APP",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        scope.launch {
                            checkAndShowAd(context) {
                                shareThisApp(context as Activity)
                            }

                        }
                    })

                RoundedButton(painter = painterResource(id = R.drawable.ic_star_solid),
                    label = "RATE THIS APP",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        scope.launch {
                            checkAndShowAd(context) {
                                rateThisApp(context as Activity)
                            }
                        }
                    })
            }
        }
    }


    fun shareThisApp(activity: Activity) {
        val myIntent = Intent(Intent.ACTION_SEND)
        myIntent.type = "text/plain"
        val url = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        val sub: String = "All Video downloader"
        val body: String =
            "Hey! This app can download any instagram and facebook video. \n Download Now its free! \n \n $url"
        myIntent.putExtra(Intent.EXTRA_SUBJECT, sub)
        myIntent.putExtra(Intent.EXTRA_TEXT, body)
        activity.startActivity(Intent.createChooser(myIntent, "Share Using"))
    }

    fun rateThisApp(activity: Activity) {
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
        )
        activity.startActivity(intent)
    }
}

@Preview
@Composable
fun PrevPageWelcome() {
    val page: Page = PageWelcome(rememberNavController())
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            page.drawContent()
        }
    }
}