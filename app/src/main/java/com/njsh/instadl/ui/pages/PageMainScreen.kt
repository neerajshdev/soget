package com.njsh.instadl.ui.pages

import android.app.Activity
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
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.njsh.instadl.R
import com.njsh.instadl.ads.AdClickCounter
import com.njsh.instadl.ads.InterstitialAdLoader
import com.njsh.instadl.ads.checkAndShowAd
import com.njsh.instadl.ads.showAd
import com.njsh.instadl.api.CallResult
import com.njsh.instadl.navigation.Page
import com.njsh.instadl.ui.components.LeftCurvedButton
import com.njsh.instadl.ui.components.RightCurvedHeading
import com.njsh.instadl.ui.theme.AppTheme

class PageMainScreen(val onNavigateTo: (String) -> Unit) : Page("Video downloader")
{
    init
    {
        addContent {
            Content()
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
                Box(modifier = Modifier.weight(1f))
                OptionsLayout(modifier = Modifier.fillMaxSize())
                Box(modifier = Modifier.weight(1f))
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
                            onNavigateTo(Route.InstagramReelScreen.name)
                        }
                    })

                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_square_facebook),
                    label = "FACEBOOK VIDEOS",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        checkAndShowAd(context) {
                            onNavigateTo(Route.FacebookVideoScreen.name)
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


@Preview
@Composable
fun PrevPageMainScreen()
{
    val page: Page = PageMainScreen() {}
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            page.drawContent()
        }
    }
}




