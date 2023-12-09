package com.gd.reelssaver.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.R
import com.gd.reelssaver.ui.components.HomeTopBar
import com.gd.reelssaver.ui.components.InputUrlFieldCard
import com.gd.reelssaver.ui.components.MediumSizeNativeAd
import com.gd.reelssaver.ui.navigation.FakeHomeScreenComponent
import com.gd.reelssaver.ui.navigation.HomeScreenComponent
import com.gd.reelssaver.ui.theme.AppTheme
import java.net.URL


@Preview
@Composable
private fun HomeScreenPreview() {
    val component = remember { FakeHomeScreenComponent() }
    AppTheme {
        HomeScreenContent(component = component)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreenContent(
    component: HomeScreenComponent,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isKeyboardOpen = WindowInsets.isImeVisible
    val focusManager = LocalFocusManager.current

    val inputText by component.inputText.subscribeAsState()
    val tabCount by component.tabCount.subscribeAsState()

    val progress by animateFloatAsState(
        targetValue = if (isKeyboardOpen) 1f else 0f, label = "progress"
    )

    val motionSceneContent = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }

    val openInputText: () -> Unit = {
        try {
            val url = URL(inputText)
            component.onEvent(HomeScreenComponent.Event.OpenWeb(url))
        } catch (ex: Exception) {
            component.onEvent(HomeScreenComponent.Event.SearchWeb(inputText))
        }
    }

    val updateInputText: (String) -> Unit = {
        component.onEvent(HomeScreenComponent.Event.UpdateInputText(it))
    }

    LaunchedEffect(key1 = isKeyboardOpen, block = {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
        }
    })

    Scaffold(
        topBar = {
            HomeTopBar(tabsCount = tabCount)
        }
    ) {
        MotionLayout(
            motionScene = MotionScene(motionSceneContent),
            progress = progress,
            modifier = modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            MediumSizeNativeAd(
                refreshTimeSec = 60,
                modifier = Modifier
                    .layoutId("ad_place_holder")
                    .padding(horizontal = 16.dp)
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

            InputUrlFieldCard(
                url = inputText,
                onValueChange = updateInputText,
                onKeyBoardAction = openInputText,
                onGoActionClick = openInputText,
                onContentPaste = updateInputText,
                modifier = Modifier
                    .layoutId("url_edit_text")
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            SocialMediaSiteCard(
                onSiteOpen = { siteUrl ->
                    component.onEvent(HomeScreenComponent.Event.OpenWeb(siteUrl))
                },
                modifier = Modifier
                    .layoutId("social_media_card")
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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