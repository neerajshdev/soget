package com.gd.reelssaver.ui.screens.downloads

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.Downloading
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.value.operator.map
import com.desidev.downloader.model.Download
import com.gd.reelssaver.ui.composables.DownloadItem


@Preview
@Composable
fun DownloadContentPreview() {
    DownloadContent(component = FakeDownloadComponent())
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadContent(component: DownloadsComponent, modifier: Modifier = Modifier) {

    val inProgress by component.downloads.map { it.filter { it.status == Download.Status.InProgress } }
        .subscribeAsState()

    val downloaded by component.downloads.map { it.filter { it.status == Download.Status.Complete } }
        .subscribeAsState()

    var selectedTabIndex = remember { mutableIntStateOf(0) }
    val tabItems = remember {
        listOf(
            TabItem("InProgress", Icons.Rounded.Downloading),
            TabItem("Downloaded", Icons.Rounded.DownloadDone)
        )
    }
    val pagerstate = rememberPagerState(0, pageCount = { tabItems.size })

    LaunchedEffect(Unit) {
        snapshotFlow { selectedTabIndex.value }.collect {
            pagerstate.scrollToPage(it)
        }
    }

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = selectedTabIndex.value) {
            tabItems.forEachIndexed { index, tabItem ->
                Tab(
                    selected = index == selectedTabIndex.value,
                    onClick = { selectedTabIndex.value = index },
                    text = { Text(text = tabItem.title) },
                    icon = { Icon(imageVector = tabItem.icon, contentDescription = null) }
                )
            }
        }
        HorizontalPager(state = pagerstate, Modifier.padding(16.dp)) { pageIndex ->
            val items = when (pageIndex) {
                0 -> inProgress
                else -> downloaded
            }

            DownloadItems(items = items, Modifier.fillMaxSize())
        }
    }
}

data class TabItem(
    val title: String,
    val icon: ImageVector
)


@Composable
fun DownloadItems(items: List<Download>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        items.forEach { item ->
            DownloadItem(
                item = item,
                modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}


