package com.gd.reelssaver.ui.screens.downloads

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.Downloading
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.value.operator.map
import com.desidev.downloader.model.Download
import com.gd.reelssaver.ui.composables.DownloadItem
import kotlinx.coroutines.launch


data class TabItem(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun DownloadScreenPreview() {
    var inProgress by remember { mutableStateOf(emptyList<Download>()) }
    var downloaded by remember { mutableStateOf(emptyList<Download>()) }
    var tabIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(initialPage = tabIndex) { 2 }
    val scope = rememberCoroutineScope()

    DownloadScreen(
        inProgress = inProgress,
        downloaded = downloaded,
        modifier = Modifier.fillMaxSize(),
        onTabChange = {
            scope.launch {
                tabIndex = it
                pagerState.animateScrollToPage(it)
            }
        },
        currentIndex = tabIndex,
        pagerState = pagerState,
        bottomNavBar = {}
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadContent(
    component: DownloadsComponent,
    modifier: Modifier = Modifier,
    bottomNavBar: @Composable () -> Unit
) {
    val downloads by component.downloads.subscribeAsState()
    val inProgress = downloads.filter { it.status == Download.Status.InProgress }
    val downloaded = downloads.filter { it.status == Download.Status.Complete }

    var tabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(initialPage = tabIndex) { 2 }
    val scope = rememberCoroutineScope()

    DownloadScreen(
        inProgress = inProgress,
        downloaded = downloaded,
        modifier = modifier,
        onTabChange = {
            scope.launch {
                tabIndex = it
                pagerState.animateScrollToPage(it)
            }
        },
        currentIndex = tabIndex,
        pagerState = pagerState,
        bottomNavBar = bottomNavBar
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadScreen(
    modifier: Modifier = Modifier,
    inProgress: List<Download>,
    downloaded: List<Download>,
    currentIndex: Int,
    pagerState: PagerState,
    bottomNavBar: @Composable () -> Unit,
    onTabChange: (Int) -> Unit,
) {
    val tabItems = remember {
        listOf(
            TabItem("InProgress", Icons.Rounded.Downloading),
            TabItem("Downloaded", Icons.Rounded.DownloadDone)
        )
    }

    LaunchedEffect(pagerState.currentPage) {
        onTabChange(pagerState.currentPage)
    }

    Scaffold(bottomBar = bottomNavBar, modifier = modifier) {
        Column(modifier = Modifier.padding(it)) {
            TabRow(selectedTabIndex = currentIndex) {
                tabItems.forEachIndexed { index, tabItem ->
                    Tab(
                        selected = index == currentIndex,
                        onClick = {
                            onTabChange(index)
                        },
                        text = { Text(text = tabItem.title) },
                        icon = { Icon(imageVector = tabItem.icon, contentDescription = null) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                Modifier.fillMaxSize()
            ) { pageIndex ->
                val items = when (pageIndex) {
                    0 -> inProgress
                    else -> downloaded
                }

                if (items.isEmpty()) {
                    val message =
                        if (pageIndex == 0) "No download in progress!" else "You have not downloaded anything!"
                    DownloadMessage(
                        text = message,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                } else {
                    DownloadItems(items = items, Modifier.fillMaxSize())
                }
            }
        }
    }
}


@Composable
fun DownloadItems(items: List<Download>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        items.forEach { item ->
            DownloadItem(
                item = item,
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun DownloadMessage(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        )
    }
}


