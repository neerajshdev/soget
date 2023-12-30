package com.gd.reelssaver.ui.screens.downloads

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Deselect
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.Downloading
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.desidev.downloader.model.Download
import com.gd.reelssaver.ui.composables.DownloadItem
import com.gd.reelssaver.ui.theme.AppTheme
import kotlinx.coroutines.launch


data class TabItem(
    val title: String,
    val icon: ImageVector
)

@Preview
@Composable
fun DownloadScreenPreview() {
    AppTheme {
        DownloadContent(component = FakeDownloadComponent()) {}
    }
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

    var selection by remember { mutableStateOf<List<Download>>(emptyList()) }


    var tabIndex by remember { mutableIntStateOf(if (inProgress.isNotEmpty()) 0 else 1) }
    val pagerState = rememberPagerState(initialPage = tabIndex) { 2 }
    val scope = rememberCoroutineScope()

    val tabItems = remember {
        listOf(
            TabItem("InProgress", Icons.Rounded.Downloading),
            TabItem("Downloaded", Icons.Rounded.DownloadDone)
        )
    }

    val onTabChange: (Int) -> Unit = { index ->
        scope.launch {
            tabIndex = index
            pagerState.animateScrollToPage(index)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onTabChange(pagerState.currentPage)
    }

    Scaffold(bottomBar = bottomNavBar, modifier = modifier) {
        Column(modifier = Modifier.padding(it)) {
            TabRow(selectedTabIndex = tabIndex) {
                tabItems.forEachIndexed { index, tabItem ->
                    Tab(
                        selected = index == tabIndex,
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
                when (pageIndex) {
                    0 -> {
                        if (inProgress.isNotEmpty()) {
                            ProgressPage(modifier = Modifier.fillMaxSize(), inProgress = inProgress)
                        } else {
                            DownloadMessage(
                                text = "No download in progress!",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                        }
                    }

                    else -> {
                        if (downloaded.isNotEmpty()) {
                            DownloadedItemsPage(
                                modifier = Modifier.fillMaxSize(),
                                downloaded = downloaded,
                                itemSelection = selection,
                                onSelectItem = {
                                    selection += it
                                },
                                onDeselectItem = {
                                    selection -= it
                                },
                                onDeleteClick = {
                                    if (selection.isNotEmpty()) {
                                        component.onEvent(Event.OnRemovedDownloadItem(selection))
                                        selection = emptyList()
                                    }
                                },
                                onSelectAll = {
                                    selection = downloaded
                                },
                                onDeselectAllClick = {
                                    selection = emptyList()
                                },
                                onItemClick = {
                                    component.onEvent(Event.OnClickDownloadedItem(it))
                                }
                            )
                        } else {
                            DownloadMessage(
                                text = "You have not downloaded anything!",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                        }

                    }
                }
            }
        }
    }

}


@Composable
fun ProgressPage(modifier: Modifier, inProgress: List<Download>) {
    DownloadItemList(
        items = inProgress,
        modifier = modifier,
        onEnableSelection = {},
        onDeselectItem = {},
        onSelectItem = {},
        onItemClick = {}
    )
}

@Composable
fun DownloadedItemsPage(
    modifier: Modifier,
    downloaded: List<Download>,
    itemSelection: List<Download>,
    onSelectItem: (Download) -> Unit,
    onDeselectItem: (Download) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAllClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onItemClick: (Download) -> Unit
) {
    var isSelectionDisable by remember { mutableStateOf(true) }

    LaunchedEffect(itemSelection) {
        isSelectionDisable = itemSelection.isEmpty()
    }

    Column(modifier) {
        SelectItemWidget(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            itemSelection = itemSelection,
            onSelectAllClick = onSelectAll,
            onDeselectAllClick = onDeselectAllClick,
            onDeleteClick = onDeleteClick
        )

        DownloadItemList(
            items = downloaded,
            isSelectionDisable = isSelectionDisable,
            modifier = Modifier.fillMaxSize(),
            selection = itemSelection,
            onSelectItem = onSelectItem,
            onDeselectItem = onDeselectItem,
            onEnableSelection = { isSelectionDisable = false },
            onItemClick = onItemClick
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadItemList(
    items: List<Download>,
    modifier: Modifier = Modifier,
    isSelectionDisable: Boolean = true,
    onItemClick: (Download) -> Unit,
    selection: List<Download> = emptyList(),
    onSelectItem: (Download) -> Unit,
    onDeselectItem: (Download) -> Unit,
    onEnableSelection: () -> Unit
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        items.forEach { item ->
            val isSelected = isSelectionDisable.not() && selection.contains(item)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                DownloadItem(
                    item = item,
                    Modifier
                        .combinedClickable(
                            onClick = {
                                if (isSelectionDisable.not()) {
                                    if (isSelected) onDeselectItem(item) else onSelectItem(item)
                                } else {
                                    onItemClick(item)
                                }
                            },
                            onLongClick = {
                                if (isSelectionDisable) {
                                    onSelectItem(item)
                                    onEnableSelection()
                                }
                            }
                        )
                )

                if (isSelected) {
                    IconButton(onClick = { }, modifier = Modifier.align(Alignment.BottomEnd)) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.surfaceTint
                        )
                    }
                }
            }
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


@Composable
fun SelectItemWidget(
    modifier: Modifier,
    itemSelection: List<Download>,
    onSelectAllClick: () -> Unit,
    onDeselectAllClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    val textStyle = MaterialTheme.typography.labelSmall

    @Composable
    fun SelectAllIconButton() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            IconButton(onClick = onSelectAllClick) {
                Icon(imageVector = Icons.Rounded.SelectAll, contentDescription = null)
            }
            Text(text = "SelectAll", style = textStyle)
        }
    }

    @Composable
    fun DeSelectAllIconButton() {
        if (itemSelection.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(onClick = onDeselectAllClick) {
                    Icon(imageVector = Icons.Rounded.Deselect, contentDescription = null)
                }
                Text(text = "Deselect All", style = textStyle)
            }
        }
    }

    @Composable
    fun DeleteIconButton() {
        if (itemSelection.isNotEmpty()) {
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
            }
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SelectAllIconButton()
        DeSelectAllIconButton()

        Spacer(modifier = Modifier.weight(1f))
        DeleteIconButton()
    }
}


