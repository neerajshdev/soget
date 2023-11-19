package com.centicbhaiya.getitsocial.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import com.centicbhaiya.getitsocial.model.FBVideoData
import com.centicbhaiya.getitsocial.ui.components.ComposeWebView
import com.centicbhaiya.getitsocial.ui.components.FacebookVideoCard
import com.centicbhaiya.getitsocial.ui.components.InputUrlFieldCard
import com.centicbhaiya.getitsocial.ui.components.TabsChooser
import com.centicbhaiya.getitsocial.ui.components.searchVideoElement
import com.centicbhaiya.getitsocial.ui.state.FbVideoDataState
import com.centicbhaiya.getitsocial.ui.state.PageType
import com.centicbhaiya.getitsocial.ui.state.Tab
import com.centicbhaiya.getitsocial.ui.state.TabsScreenState
import com.centicbhaiya.getitsocial.ui.state.clearAll
import com.centicbhaiya.getitsocial.ui.state.closeWebPage
import com.centicbhaiya.getitsocial.ui.state.goto
import com.centicbhaiya.getitsocial.ui.state.newTab
import com.centicbhaiya.getitsocial.ui.state.removeTab
import com.centicbhaiya.getitsocial.ui.state.selectTab
import com.centicbhaiya.getitsocial.ui.theme.AppTheme
import com.centicbhaiya.getitsocial.ui.theme.useDarkTheme
import kotlinx.coroutines.launch
import online.desidev.onestate.OneState
import online.desidev.onestate.stateManager
import online.desidev.onestate.toState

private const val TAG = "TabsScreen"

@Preview
@Composable
fun TabsScreenPrev() {
    stateManager.configure {
        stateFactory(FbVideoDataState::class) {
            FbVideoDataState(emptyList())
        }

        stateFactory(TabsScreenState::class) {
            TabsScreenState()
        }
    }

    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TabsScreen(tabsScreenState = stateManager.getState(TabsScreenState::class))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsScreen(
    tabsScreenState: OneState<TabsScreenState>,
    onDownloadVideo: (FBVideoData) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val currentTab by tabsScreenState.toState { it.getCurrentTab() }
    val tabs by tabsScreenState.toState(convert = { it.tabs })
    val bottomSheetState = rememberModalBottomSheetState()

    val fbVideoDataState = stateManager.getState(FbVideoDataState::class)
    val videoDataList by fbVideoDataState.toState { it.list }

    var showSearchVideos by remember { mutableStateOf(false) }
    var showTabsChooser by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopBar(
            tabsCount = tabs.size, onTabsListOpen = {
                showTabsChooser = true
            }, modifier = Modifier.fillMaxWidth()
        )
    }, floatingActionButton = {
        FilledIconButton(onClick = {
            showSearchVideos = true
            currentTab.webView?.let {
                scope.launch {
                    val list = searchVideoElement(it)
                    fbVideoDataState.send { FbVideoDataState(list) }
                }
            }
        }) {
            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = "Click to Download Videos!"
            )
        }
    }) { innerPadding ->
        Box {
            Crossfade(
                currentTab.pageType,
                label = "crossfade",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { pageType ->
                when (pageType) {
                    PageType.HOMEPAGE -> {
                        HomePage(
                            onUrlEnter = { pageUrl ->
                                tabsScreenState.goto(pageUrl)
                            },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    PageType.WEBPAGE -> {
                        currentTab.url?.let { pageUrl ->
                            BackHandler {
                                tabsScreenState.closeWebPage()
                            }

                            ComposeWebView(
                                initialUrl = pageUrl,
                                webView = currentTab.webView,
                                onCreate = { webView ->
                                    tabsScreenState.send {
                                        it.updateCurrentTab(
                                            it.getCurrentTab().copy(webView = webView)
                                        )
                                    }
                                },
                                onPageLoad = {}
                            )
                        }
                    }
                }
            }

//            BottomSheetHandleIcon(
//                onClick = {
//                    showSearchVideos = true
//                    currentTab.webView?.let {
//                        scope.launch {
//                            val list = searchVideoElement(it)
//                            fbVideoDataState.send { FbVideoDataState(list) }
//                        }
//                    }
//                }, modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .offset(y = (56 / 2).dp)
//            )
        }

        if (showSearchVideos || showTabsChooser) {
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                onDismissRequest = { showSearchVideos = false; showTabsChooser = false },
                sheetState = bottomSheetState
            ) {
                if (showSearchVideos) {
                    ShowSearchedVideos(
                        videoDataList = videoDataList, onDownloadVideo = onDownloadVideo
                    )
                } else {
                    TabsChooser(tabs = tabs,
                        selectedTab = currentTab,
                        onRemoveTab = { tabsScreenState.removeTab(it) },
                        onClearAllTabs = { tabsScreenState.clearAll() },
                        onForwardClick = {
                            currentTab.webView?.let {
                                if (it.canGoForward()) {
                                    it.goForward()
                                }
                            }
                        },
                        onBackClick = {
                            currentTab.webView?.let {
                                if (it.canGoBack()) {
                                    it.goBack()
                                }
                            }
                        },
                        onAddTabClick = { tabsScreenState.newTab() },
                        onTabSelect = { tab -> tabsScreenState.selectTab(tab) }
                    )
                }
            }

            LaunchedEffect(key1 = bottomSheetState) {
                bottomSheetState.show()
            }
        }
    }
}


@Composable
fun ShowSearchedVideos(videoDataList: List<FBVideoData>, onDownloadVideo: (FBVideoData) -> Unit) {
    Text(
        text = "Videos on this page",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(16.dp)
    )

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
                    FacebookVideoCard(videoData = it,
                        modifier = Modifier.padding(16.dp),
                        onDownloadClick = {
                            onDownloadVideo(it)
//                                        fetchDownloader.downloadFile(it.videoUrl, createFileName("$appName.mp4"))
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomePage(modifier: Modifier = Modifier, onUrlEnter: (String) -> Unit) {
    val context = LocalContext.current
    val isKeyboardOpen = WindowInsets.isImeVisible
    val focusManager = LocalFocusManager.current

    var url by remember { mutableStateOf("") }

    Log.d(TAG, "iskeyboardVisible: $isKeyboardOpen")

    val progress by animateFloatAsState(
        targetValue = if (isKeyboardOpen) 1f else 0f, label = "progress"
    )

    val motionSceneContent = remember {
        context.resources.openRawResource(R.raw.motion_scene).readBytes().decodeToString()
    }

    LaunchedEffect(key1 = isKeyboardOpen, block = {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
        }
    })

    Column(modifier = modifier) {
        MotionLayout(
            motionScene = MotionScene(motionSceneContent),
            progress = progress,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.drawable.native_ad_placeholder),
                contentDescription = "ad_placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .layoutId("ad_place_holder")
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .height(200.dp)
            )

            InputUrlFieldCard(
                url = url,
                onValueChange = { url = it },
                onKeyBoardAction = { onUrlEnter(url) },
                onGoActionClick = { onUrlEnter(url) },
                onContentPaste = { url = it },
                modifier = Modifier
                    .layoutId("url_edit_text")
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            SocialMediaSiteCard(
                onGotoFb = { onUrlEnter("https://www.facebook.com/") },
                onGotoInstagram = { onUrlEnter("https://www.instagram.com/") },
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
fun TopBar(
    modifier: Modifier,
    tabsCount: Int,
    onTabsListOpen: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        modifier = modifier,
        title = { Text(stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = onTabsListOpen) {
                PageCountIcon(count = tabsCount)
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
        onClick = onClick, modifier = modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowUp,
            contentDescription = "Search videos",
            modifier = Modifier
                .size(36.dp)
                .offset(0.dp, (-56 / 4).dp)
        )
    }
}

@Composable
fun PageCountIcon(count: Int) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(20.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(4.dp)
            ), contentAlignment = Alignment.Center
    ) {
        Text(text = count.toString(), style = MaterialTheme.typography.titleSmall)
    }
}


@Composable
fun SocialMediaSiteCard(
    modifier: Modifier = Modifier,
    onGotoFb: () -> Unit,
    onGotoInstagram: () -> Unit
) {
    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Facebook(onClick = onGotoFb)
            Instagram(onClick = onGotoInstagram)
        }
    }
}


@Composable
fun Facebook(onClick: () -> Unit, iconSize: Dp = 56.dp) {
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
fun Instagram(onClick: () -> Unit, iconSize: Dp = 56.dp) {
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
fun SocialSite(name: String, icon: @Composable () -> Unit) {
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