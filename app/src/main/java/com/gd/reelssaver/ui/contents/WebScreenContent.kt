package com.gd.reelssaver.ui.contents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gd.reelssaver.R
import com.gd.reelssaver.ads.InterstitialAdManager
import com.gd.reelssaver.model.VideoData
import com.gd.reelssaver.ui.blocs.WebScreenComponent
import com.gd.reelssaver.ui.blocs.WebScreenComponent.Event
import com.gd.reelssaver.ui.composables.BrowserTopBar
import com.gd.reelssaver.ui.composables.ComposeWebView
import com.gd.reelssaver.ui.composables.SearchVideoCard
import com.gd.reelssaver.ui.util.storagePermission


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebScreenContent(component: WebScreenComponent) {
    var showFoundVideos by remember { mutableStateOf(false) }

    var shouldAskPermission by remember { mutableStateOf(false) }
    val storagePermission = storagePermission(shouldAskPermission)

    val view by component.webView.collectAsState()
    val currentUrl by component.currentUrl.collectAsState()
    val videoOnPage by component.videosOnPage.collectAsState()
    val pageCount by component.pageCount.collectAsState()
    val isDarkTheme by component.isDarkTheme.collectAsState()

    Scaffold(
        topBar = {
            BrowserTopBar(
                isDarkTheme = isDarkTheme,
                currentUrl = currentUrl,
                pageCount = pageCount,
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
                component.onEvent(Event.GetVideosOnPage)
                showFoundVideos = true
                InterstitialAdManager.tryAd()
            }) {
                Icon(
                    imageVector = Icons.Rounded.Download,
                    contentDescription = "Click to Download Videos!"
                )
            }
        }
    ) { padding ->
        ComposeWebView(
            modifier = Modifier.padding(padding),
            initialUrl = currentUrl,
            webView = view,
            onCreate = { webView ->
                component.onEvent(
                    Event.OnWebViewCreated(
                        webView
                    )
                )
            },
            onPageLoad = {view, newUrl ->
                component.onEvent(Event.OnPageLoaded(newUrl))
            }
        )

        val appname = stringResource(id = R.string.app_name)
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        if (showFoundVideos) {
            ModalBottomSheet(
                onDismissRequest = { showFoundVideos = false },
                sheetState = sheetState
            ) {
                SearchedVideos(
                    videoDataList = videoOnPage,
                    onDownloadVideo = { videoData ->
                        if (storagePermission) {
                            component.onEvent(
                                Event.DownloadVideo(
                                    videoData,
                                    appname
                                )
                            )
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
    }

    BackHandler {
        component.onEvent(Event.OnGoBack)
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
//                          fetchDownloader.downloadFile(it.videoUrl, createFileName("$appName.mp4"))
                        })
                }
            }
        }
    }
}