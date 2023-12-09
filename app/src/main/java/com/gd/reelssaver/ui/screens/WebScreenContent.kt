package com.gd.reelssaver.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.model.VideoData
import com.gd.reelssaver.ui.components.BrowserTopBar
import com.gd.reelssaver.ui.components.ComposeWebView
import com.gd.reelssaver.ui.components.SearchVideoCard
import com.gd.reelssaver.ui.navigation.WebScreenComponent
import com.gd.reelssaver.ui.navigation.WebScreenComponent.Event
import com.gd.reelssaver.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebScreenContent(component: WebScreenComponent) {
    val activeTab by component.activeTab.subscribeAsState()
    val tabs by component.tabs.subscribeAsState()
    val videoOnPage by component.videosOnPage.subscribeAsState()
    var showFoundVideos by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            BrowserTopBar(
                currentUrl = activeTab.url,
                tabCount = tabs.size,
                onOpenTabChooser = { component.onEvent(Event.OpenTabChooser) },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp)
            )
        },
        floatingActionButton = {
            FilledIconButton(onClick = { component.onEvent(Event.GetVideosOnPage) }) {
                Icon(
                    imageVector = Icons.Rounded.Download,
                    contentDescription = "Click to Download Videos!"
                )
            }
        }
    ) { it ->
        ComposeWebView(
            modifier = Modifier.padding(it),
            initialUrl = activeTab.url,
            webView = component.views[activeTab.id],
            onCreate = { webView ->
                component.onEvent(
                    Event.WebViewCreated(
                        webView
                    )
                )
            },
            onPageLoad = { newUrl ->
                component.onEvent(Event.UpdateUrl(newUrl))
            }
        )

        val appname = stringResource(id = R.string.app_name)
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        if (showFoundVideos) {
            ModalBottomSheet(
                onDismissRequest = { showFoundVideos = false },
                sheetState = sheetState
            ) {
                ShowSearchedVideos(
                    videoDataList = videoOnPage,
                    onDownloadVideo = { videoData ->
                        // Todo: Storage Permission is required to write
                        component.onEvent(
                            Event.DownloadVideo(
                                videoData,
                                appname
                            )
                        )
                    }
                )
            }

            LaunchedEffect(key1 = Unit) {
                sheetState.show()
            }
        }

    }
}

@Composable
private fun ShowSearchedVideos(
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
//                                        fetchDownloader.downloadFile(it.videoUrl, createFileName("$appName.mp4"))
                        })
                }
            }
        }
    }
}