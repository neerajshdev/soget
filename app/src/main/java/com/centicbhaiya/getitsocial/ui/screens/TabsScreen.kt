package com.centicbhaiya.getitsocial.ui.screens

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.centicbhaiya.getitsocial.R
import com.centicbhaiya.getitsocial.ui.components.ComposeWebView
import com.centicbhaiya.getitsocial.ui.components.FacebookVideoCard
import com.centicbhaiya.getitsocial.ui.components.InputUrlFieldCard
import com.centicbhaiya.getitsocial.ui.state.FbVideoDataState
import com.centicbhaiya.getitsocial.ui.state.Tab
import com.centicbhaiya.getitsocial.ui.state.TabsScreenState
import com.centicbhaiya.getitsocial.ui.theme.AppTheme
import com.centicbhaiya.getitsocial.ui.theme.useDarkTheme
import kotlinx.coroutines.launch
import online.desidev.onestate.OneState
import online.desidev.onestate.rememberOneState
import online.desidev.onestate.stateManager
import online.desidev.onestate.toState


private const val TAG = "HOME_SCREEN"

@Preview
@Composable
fun HomePagePrev() {
    stateManager.configure {
        stateFactory(FbVideoDataState::class) {
            FbVideoDataState(emptyList())
        }

        stateFactory(TabsScreenState::class) {
            TabsScreenState(
                tabs = listOf(Tab("homepage")),
            )
        }
    }

    val tabsScreenState = stateManager.rememberOneState(TabsScreenState::class)

    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomePage(tabsScreenState = tabsScreenState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsScreen(modifier: Modifier = Modifier) {
    val tabsScreenState = stateManager.rememberOneState(TabsScreenState::class)
    val currentTab by tabsScreenState.toState { it.getCurrentTabUrl() }
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    val fbVideoDataState = stateManager.getState(FbVideoDataState::class)
    val videoDataList by fbVideoDataState.toState { it.list }


    Box(modifier = modifier) {
        Crossfade(currentTab.url, label = "crossfade", modifier = Modifier.fillMaxSize()) { url ->
            if (url == "homepage") {
                HomePage(tabsScreenState = tabsScreenState)
            } else {
                ComposeWebView(url = url)
            }
        }

        BottomSheetHandleIcon(
            onClick = { isBottomSheetVisible = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 20.dp)
        )

        if (isBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { isBottomSheetVisible = false },
                sheetState = bottomSheetState
            ) {
                if (videoDataList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(250.dp)
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "There is no video on this page! Please go to supported url",
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                    }
                } else {
                    videoDataList.forEach {
                        Column {
                            key(it) {
                                FacebookVideoCard(videoData = it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomePage(modifier: Modifier = Modifier, tabsScreenState: OneState<TabsScreenState>) {
    val context = LocalContext.current
    val isKeyboardOpen = WindowInsets.isImeVisible
    val focusManager = LocalFocusManager.current


    var url by remember { mutableStateOf("") }

    Log.d(TAG, "iskeyboardVisible: $isKeyboardOpen")

    val progress by animateFloatAsState(
        targetValue = if (isKeyboardOpen) 1f else 0f,
        label = "progress"
    )

    val motionSceneContent = remember {
        context.resources.openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }

    LaunchedEffect(
        key1 = isKeyboardOpen,
        block = {
            if (!isKeyboardOpen) {
                focusManager.clearFocus()
            }
        }
    )

    Column(modifier = modifier) {
        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        MotionLayout(
            motionScene = MotionScene(motionSceneContent),
            progress = progress,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.native_ad_placeholder),
                contentDescription = "ad_placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .layoutId("ad_place_holder")
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .height(350.dp)
            )

            InputUrlFieldCard(
                url = url,
                onValueChange = { url = it },
                onKeyBoardAction = {
                    tabsScreenState.send { it.updateUrl(url) }
                },
                onGoActionClick = {
                    tabsScreenState.send { it.updateUrl(url) }
                },
                onContentPaste = { url = it },
                modifier = Modifier
                    .layoutId("url_edit_text")
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            SocialMediaSiteCard(
                modifier = Modifier
                    .layoutId("social_media_card")
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        modifier = modifier,
        title = { Text(stringResource(id = R.string.app_name)) },
        actions = {  // Adding the theme change icon button

            IconButton(onClick = { /*TODO*/ }) {
                PageCountIcon(count = 1)
            }

            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "How to use")
            }

            IconButton(onClick = {
                scope.launch {
                    useDarkTheme = useDarkTheme.not()
                }
            }) {
                Icon(
                    painter = painterResource(id = if (useDarkTheme) R.drawable.baseline_dark_mode_24 else R.drawable.baseline_light_mode_24),
                    contentDescription = "Change Theme",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}


@Composable
fun BottomSheetHandleIcon(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowUp,
            contentDescription = "Search videos",
            modifier = Modifier.offset(0.dp, (-10).dp)
        )
    }
}

@Composable
fun PageCountIcon(count: Int) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(6.dp)
            ), contentAlignment = Alignment.Center
    ) {
        Text(text = count.toString(), style = MaterialTheme.typography.titleSmall)
    }
}


@Composable
fun SocialMediaSiteCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Facebook(onClick = {})
            Instagram(onClick = {})
        }
    }
}


@Composable
fun Facebook(onClick: () -> Unit, iconSize: Dp = 56.dp) {
    SocialSite(
        name = "Facebook",
        icon = {
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(iconSize)
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
fun Instagram(onClick: () -> Unit, iconSize: Dp = 56.dp) {
    SocialSite(
        name = "Instagram",
        icon = {
            IconButton(
                onClick = onClick, modifier = Modifier
                    .size(iconSize)
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
fun SocialSite(name: String, icon: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}