package com.njsh.instadl.ui.pages

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.njsh.instadl.MainActivity
import com.njsh.instadl.R
import com.njsh.instadl.ads.checkAndShowAd
import com.njsh.instadl.navigation.Page
import com.njsh.instadl.ui.components.LeftCurvedButton
import com.njsh.instadl.ui.components.NativeAdView
import com.njsh.instadl.ui.components.RightCurvedHeading
import com.njsh.instadl.ui.theme.AppTheme

class PageMainScreen(val navController: NavController) : Page("Video downloader")
{
    init
    {
        addContent {
            Content()
            val activity = LocalContext.current as MainActivity
            BackHandler {
                checkAndShowAd(activity) {
                    navController.popBackStack()
                }
            }
        }
    }

    @Composable
    private fun Content()
    {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    RightCurvedHeading(label = "ALL VIDEO DOWNLOADER")
                }
                NativeAdView(modifier = Modifier.padding(16.dp))
                OptionsLayout(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f))
                NativeAdView(modifier = Modifier.padding(16.dp))
            }
        }

    }

    @Composable
    private fun OptionsLayout(modifier: Modifier = Modifier)
    {
        val context = LocalContext.current as Activity
        Box(contentAlignment = Alignment.CenterEnd, modifier = modifier.fillMaxWidth()) {
            Column {
                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_instagram),
                    label = "INSTAGRAM REEL",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        checkAndShowAd(context) {
                            navController.navigate(Route.InstagramReelScreen.name)
                        }
                    })

                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_square_facebook),
                    label = "FACEBOOK VIDEOS",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        checkAndShowAd(context) {
                            navController.navigate(Route.FacebookVideoScreen.name)
                        }
                    })

              /*  LeftCurvedButton(painter = painterResource(id = R.drawable.ic_youtube),
                    label = "YOUTUBE VIDEOS",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {})*/
            }
        }
    }
}


/*@Preview
@Composable
fun PrevPageMainScreen()
{
    val page: Page = PageMainScreen() {}
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            page.drawContent()
        }
    }
}*/




