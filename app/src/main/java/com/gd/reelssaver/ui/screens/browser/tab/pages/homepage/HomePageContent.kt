package com.gd.reelssaver.ui.screens.browser.tab.pages.homepage


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.R
import com.gd.reelssaver.ads.InterstitialAdManager
import com.gd.reelssaver.ui.composables.HomeTopBar
import com.gd.reelssaver.ui.composables.InputUrlFieldCard
import com.gd.reelssaver.ui.composables.MediumSizeNativeAd
import com.gd.reelssaver.ui.theme.AppTheme
import java.net.URL


@Preview(wallpaper = Wallpapers.NONE)
@Composable
private fun HomepageContentPreview() {
    val component = remember { FakeHomePage() }
    AppTheme {
        HomepageContent(component = component)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomepageContent(
    component: HomePageComponent,
    modifier: Modifier = Modifier
) {
    val useDarkTheme by component.isDarkTheme.subscribeAsState()
    LocalContext.current
    val isKeyboardOpen = WindowInsets.isImeVisible
    val focusManager = LocalFocusManager.current

    val inputText by component.inputText.subscribeAsState()
    val pageCount by component.tabsCount.subscribeAsState()

    val openInputText: () -> Unit = {
        try {
            val url = URL(inputText)
            component.onEvent(Event.OnOpenWebSite(url))
        } catch (ex: Exception) {
            component.onEvent(Event.SearchWeb(inputText))
        }
    }

    val updateInputText: (String) -> Unit = {
        component.onEvent(Event.UpdateInputText(it))
    }

    LaunchedEffect(key1 = isKeyboardOpen, block = {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
        }
    })

    Scaffold(
        topBar = {
            HomeTopBar(
                tabsCount = pageCount,
                useDarkTheme = useDarkTheme,
                onOpenTabs = { component.onEvent(Event.OpenTabChooser) },
                onToggleTheme = { component.onEvent(Event.ToggleTheme) }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .padding(it)
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            InputUrlFieldCard(
                url = inputText,
                onValueChange = updateInputText,
                onKeyBoardAction = openInputText,
                onGoActionClick = openInputText,
                onContentPaste = updateInputText,
                modifier = Modifier
                    .layoutId("url_edit_text")
                    .fillMaxWidth()
            )

            MediumSizeNativeAd(
                refreshTimeSec = 80,
                modifier = Modifier
                    .layoutId("ad_place_holder")
            ) { // ad_place_holder
                Image(
                    painter = painterResource(id = R.drawable.ad_placeholder),
                    contentDescription = "ad_placeholder",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxSize()
                )
            }

            SocialMediaSiteCard(
                onSiteOpen = { siteUrl ->
                    component.onEvent(Event.OnOpenWebSite(siteUrl))
                    InterstitialAdManager.tryAd()
                },
                modifier = Modifier
                    .layoutId("social_sites")
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SocialMediaSiteCard(
    modifier: Modifier = Modifier,
    onSiteOpen: (URL) -> Unit
) {
    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Facebook(onClick = { onSiteOpen(URL("https://www.facebook.com/")) })
            Instagram(onClick = { onSiteOpen(URL("https://www.instagram.com/")) })
            Google(onClick = { onSiteOpen(URL("https://www.google.com/")) })
        }
    }
}


@Composable
private fun Facebook(onClick: () -> Unit, iconSize: Dp = 56.dp) {
    SocialSite(
        name = "Facebook",
        icon = {
            IconButton(
                onClick = onClick, modifier = Modifier.size(iconSize)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "facebook",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                )
            }
        },
    )
}


@Composable
private fun Instagram(onClick: () -> Unit, iconSize: Dp = 56.dp) {
    SocialSite(
        name = "Instagram",
        icon = {
            IconButton(
                onClick = onClick, modifier = Modifier.size(iconSize)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.instagram),
                    contentDescription = "instagram",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                )
            }
        },
    )
}

@Composable
private fun Google(onClick: () -> Unit, iconSize: Dp = 56.dp) {
    SocialSite(
        name = "Google",
        icon = {
            IconButton(
                onClick = onClick, modifier = Modifier.size(iconSize)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google search",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                )
            }
        },
    )
}


@Composable
fun Reels(onClick: () -> Unit, iconSize: Dp = 56.dp) {
    SocialSite(name = "Reels") {

    }
}

@Composable
private fun SocialSite(name: String, icon: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}