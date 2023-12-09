package com.gd.reelssaver.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.gd.reelssaver.R
import com.gd.reelssaver.model.VideoData
import com.gd.reelssaver.ui.components.BrowserTopBar
import com.gd.reelssaver.ui.components.ComposeWebView
import com.gd.reelssaver.ui.components.InputUrlFieldCard
import com.gd.reelssaver.ui.components.SearchVideoCard
import com.gd.reelssaver.ui.components.TabsChooser
import com.gd.reelssaver.ui.components.HomeTopBar
import com.gd.reelssaver.ui.components.MediumSizeNativeAd
import com.gd.reelssaver.ui.components.searchVideoElement
import com.gd.reelssaver.ui.state.FbVideoDataState
import com.gd.reelssaver.ui.state.PageType
import com.gd.reelssaver.ui.state.TabsScreenState
import com.gd.reelssaver.ui.state.clearAll
import com.gd.reelssaver.ui.state.closeWebPage
import com.gd.reelssaver.ui.state.goto
import com.gd.reelssaver.ui.state.newTab
import com.gd.reelssaver.ui.state.removeTab
import com.gd.reelssaver.ui.state.selectTab
import com.gd.reelssaver.ui.state.updateCurrentTab
import com.gd.reelssaver.ui.state.updateTabUrl
import com.gd.reelssaver.ui.theme.AppTheme
import kotlinx.coroutines.launch
import okhttp3.internal.filterList
import online.desidev.onestate.OneState
import online.desidev.onestate.stateManager
import online.desidev.onestate.toState
import java.net.URL

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
    onDownloadVideo: (VideoData) -> Unit = {}
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
        if (currentTab.pageType == PageType.HOMEPAGE) {
            HomeTopBar(
                tabsCount = tabs.size,
                onOpenTabs = {
                    showTabsChooser = true
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Surface {
                BrowserTopBar(
                    currentUrl = currentTab.url ?: "",
                    onLoadNewPage = { loadNewPage(tabsScreenState, it) },
                    tabCount = tabs.size,
                    onOpenTabChooser = { showTabsChooser = true },
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 8.dp)
                )
            }
        }
    }, floatingActionButton = {
        FilledIconButton(onClick = {
            showSearchVideos = true
            currentTab.webView?.let {
                scope.launch {
                    val list = searchVideoElement(it).filterList { videoUrl.startsWith("http") }
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
                                loadNewPage(tabsScreenState, pageUrl)
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
                                    tabsScreenState.updateCurrentTab(currentTab.copy(webView = webView))
                                },
                                onPageLoad = { newUrl ->
                                    tabsScreenState.updateTabUrl(newUrl)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showSearchVideos || showTabsChooser) {
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                onDismissRequest = { showSearchVideos = false; showTabsChooser = false },
                sheetState = bottomSheetState
            ) {
                if (showSearchVideos) {
                    ShowSearchedVideos(
                        videoDataList = videoDataList,
                        onDownloadVideo = onDownloadVideo
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
fun ShowSearchedVideos(videoDataList: List<VideoData>, onDownloadVideo: (VideoData) -> Unit) {
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
                    SearchVideoCard(videoData = it,
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
                onSiteOpen = onUrlEnter,
                modifier = Modifier
                    .layoutId("social_media_card")
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
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
private fun SocialMediaSiteCard(
    modifier: Modifier = Modifier,
    onSiteOpen: (String) -> Unit
) {
    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Facebook(onClick = { onSiteOpen("https://www.facebook.com/") })
            Instagram(onClick = { onSiteOpen("https://www.instagram.com/") })
            Google(onClick = { onSiteOpen("https://www.google.com/") })
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
fun Google(onClick: () -> Unit, iconSize: Dp = 56.dp) {
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


private fun loadNewPage(tabsScreenState: OneState<TabsScreenState>, str: String) {
    val url = try {
        URL(str)
    } catch (ex: Exception) {
        URL("https://www.google.com/search?q=$str")
    }
    tabsScreenState.goto(url.toString())
}