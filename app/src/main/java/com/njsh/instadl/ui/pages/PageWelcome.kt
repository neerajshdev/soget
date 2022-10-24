package com.njsh.instadl.ui.pages

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd
import com.njsh.instadl.BuildConfig
import com.njsh.instadl.MainActivity
import com.njsh.instadl.R
import com.njsh.instadl.ads.NativeAdLoader
import com.njsh.instadl.ads.checkAndShowAd
import com.njsh.instadl.navigation.Page
import com.njsh.instadl.ui.components.LeftCurvedButton
import com.njsh.instadl.ui.components.NativeAdView
import com.njsh.instadl.ui.components.RightCurvedHeading
import com.njsh.instadl.ui.theme.AppTheme

class PageWelcome(val navController: NavController) : Page() {
    init {
        addContent {
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                Content()
            }

            val activity = LocalContext.current as MainActivity
            BackHandler {
                checkAndShowAd(activity) {
                    navController.navigate(Route.ExitDialog.name)
                }
            }
        }
    }


    @Composable
    private fun Content() {
        Column(
            Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                RightCurvedHeading(label = "ALL VIDEO DOWNLOADER")
            }
            NativeAdView(modifier = Modifier.padding(16.dp))
            OptionsLayout(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f))
            NativeAdView(modifier = Modifier.padding(16.dp))
        }
    }


    @Composable
    private fun OptionsLayout(modifier: Modifier = Modifier) {
        val activity = LocalContext.current as Activity
        Box(contentAlignment = Alignment.CenterEnd, modifier = modifier) {
            Column {
                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_right_to_bracket_solid),
                    label = "ENTER",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        checkAndShowAd(activity) { navController.navigate(Route.MainScreen.name) }
                    })

                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_share_nodes_solid),
                    label = "SHARE THIS APP",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        checkAndShowAd(activity) {
                            shareThisApp(activity)
                        }
                    })

                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_star_solid),
                    label = "RATE THIS APP",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        checkAndShowAd(activity) {
                            rateThisApp(activity)
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