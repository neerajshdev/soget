package com.gd.reelssaver.ui.screens.browser.tab.pages.webpage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.R
import com.gd.reelssaver.ads.InterstitialAdManager
import com.gd.reelssaver.model.VideoData
import com.gd.reelssaver.ui.composables.BrowserTopBar
import com.gd.reelssaver.ui.composables.ComposeWebView
import com.gd.reelssaver.ui.composables.SearchVideoCard
import com.gd.reelssaver.ui.util.storagePermission
import com.gd.reelssaver.util.asSome
import com.gd.reelssaver.util.isSome
import kotlinx.coroutines.launch

@Composable
fun WebpageContent(
    modifier: Modifier = Modifier,
    component: WebpageComponent,
    bottomNavBar: @Composable () -> Unit
) {
    val showFoundVideos by component.showSearchedVideos.subscribeAsState()
    val model by component.model.subscribeAsState()
    val isDarkTheme by component.isDarkTheme.subscribeAsState()
    val tabsCount by component.tabsCount.subscribeAsState()
    val searchedVideos by component.searchedVideos.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
        bottomBar = bottomNavBar,
        topBar = {
            BrowserTopBar(
                isDarkTheme = isDarkTheme,
                currentUrl = model.pageUrl,
                pageCount = tabsCount,
                onLoadNewPage = { str -> component.onEvent(Event.LoadUrl(str)) },
                onToggleTheme = { component.onEvent(Event.ToggleTheme) },
                onOpenTabChooser = { component.onEvent(Event.OpenTabChooser) },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 8.dp)
            )
        },
        floatingActionButton = {
            FilledIconButton(onClick = {
                component.onEvent(Event.OpenSearchedVideo)
                InterstitialAdManager.tryAd()
            }) {
                Icon(
                    imageVector = Icons.Rounded.Download,
                    contentDescription = "Click to Download Videos!"
                )
            }
        }
    ) { padding ->
        val view = if (model.view.isSome()) {
            model.view.asSome().value
        } else {
            null
        }

        ComposeWebView(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            initialUrl = model.pageUrl,
            webView = view,
            onCreate = { webView ->
                component.onEvent(Event.OnWebViewCreated(webView))
            },
            onPageLoad = { view, newUrl ->
                component.onEvent(Event.OnPageLoaded(newUrl, view.title ?: "unknown"))
            }
        )

        if (showFoundVideos) {
            SearchedVideoBottomSheet(
                searchedVideos = searchedVideos,
                onDismissed = { component.onEvent(Event.OnSearchVideoDismiss) },
                onVideoDownloadRequest = {
                    // close the bottom sheet
                    component.onEvent(Event.OnSearchVideoDismiss)

                    component.onEvent(Event.DownloadVideo(it, onDownloadAdd = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Video Added to Download!")
                        }
                    }, onFailed = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Something went wrong!")
                        }
                    }))
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchedVideoBottomSheet(
    searchedVideos: List<VideoData>,
    onDismissed: () -> Unit,
    onVideoDownloadRequest: (VideoData) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var shouldAskPermission by remember { mutableStateOf(false) }
    val storagePermission = storagePermission(shouldAskPermission)


    ModalBottomSheet(
        onDismissRequest = onDismissed,
        sheetState = sheetState
    ) {
        SearchedVideos(
            videoDataList = searchedVideos,
            onDownloadVideo = { videoData ->
                if (storagePermission) {
                    onVideoDownloadRequest(videoData)
                } else {
                    shouldAskPermission = true
                }
                InterstitialAdManager.tryAd()
            }
        )
    }

    LaunchedEffect(key1 = Unit) {
        sheetState.show()
    }
}


@Composable
private fun SearchedVideos(
    videoDataList: List<VideoData>,
    onDownloadVideo: (VideoData) -> Unit
) {
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
                        })
                }
            }
        }
    }
}