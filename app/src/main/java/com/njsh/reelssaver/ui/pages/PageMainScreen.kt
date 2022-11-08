package com.njsh.reelssaver.ui.pages

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
import com.njsh.reelssaver.R
import com.njsh.reelssaver.ads.checkAndShowAd
import com.njsh.reelssaver.navigation.Page
import com.njsh.reelssaver.ui.components.NativeAdView
import com.njsh.reelssaver.ui.components.RoundedButton
import com.njsh.reelssaver.ui.components.TopAppbar
import com.njsh.reelssaver.ui.theme.AppTheme
import kotlinx.coroutines.launch

class PageMainScreen(private val navController: NavController) : Page("Video downloader") {
    private val topAppBar by lazy { TopAppbar(appTitle) }

    init {
        addContent {
            Content()
            val activity = LocalContext.current
            val scope = rememberCoroutineScope()

            if (doBackPressAds) {
                BackHandler {
                    scope.launch {
                        checkAndShowAd(activity) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Content() {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                topAppBar.drawContent()
                NativeAdView(modifier = Modifier.padding(16.dp))
                OptionsLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }

    @Composable
    private fun OptionsLayout(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxWidth()) {
            Column {
                RoundedButton(painter = painterResource(id = R.drawable.ic_instagram),
                    label = "INSTAGRAM REEL",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        scope.launch {
                            checkAndShowAd(context) {
                                navController.navigate(Route.InstagramReelScreen.name)
                            }
                        }
                    })

                RoundedButton(painter = painterResource(id = R.drawable.ic_square_facebook),
                    label = "FACEBOOK VIDEOS",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {
                        scope.launch {
                            checkAndShowAd(context) {
                                navController.navigate(Route.FacebookVideoScreen.name)
                            }
                        }
                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun PrevPageMainScreen() {
    val page: Page = PageMainScreen(rememberNavController())
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            page.drawContent()
        }
    }
}




