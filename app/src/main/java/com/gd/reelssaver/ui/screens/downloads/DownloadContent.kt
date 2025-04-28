package com.gd.reelssaver.ui.screens.downloads

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.FileDownloadOff
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Deselect
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.Downloading
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.desidev.downloader.model.Download
import com.gd.reelssaver.ui.composables.BannerNativeAd
import com.gd.reelssaver.ui.composables.DownloadItem
import com.gd.reelssaver.ui.composables.Shimmer
import com.gd.reelssaver.ui.theme.AppTheme
import kotlinx.coroutines.launch


data class TabItem(
    val title: String,
    val icon: ImageVector,
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
    bottomNavBar: @Composable () -> Unit,
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
            TabItem("In Progress", Icons.Rounded.Downloading),
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

    Scaffold(
        bottomBar = bottomNavBar,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Enhanced Tab Row
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                        color = MaterialTheme.colorScheme.primary,
                        height = 3.dp
                    )
                }
            ) {
                tabItems.forEachIndexed { index, tabItem ->
                    Tab(
                        selected = index == tabIndex,
                        onClick = {
                            onTabChange(index)
                        },
                        text = {
                            Text(
                                text = tabItem.title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (index == tabIndex) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = tabItem.icon,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        )
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                Modifier.fillMaxSize()
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> {
                        ProgressPage(modifier = Modifier.fillMaxSize(), inProgress = inProgress)
                    }

                    else -> {
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
                    }
                }
            }
        }
    }
}


@Composable
fun ProgressPage(modifier: Modifier, inProgress: List<Download>) {
    Column(modifier) {
        BannerNativeAd(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Shimmer()
        }

        if (inProgress.isEmpty()) {
            EmptyStateMessage(
                icon = Icons.Outlined.FileDownloadOff,
                title = "No Active Downloads",
                message = "Your downloads in progress will appear here",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            DownloadItemList(
                downloads = inProgress,
                modifier = Modifier.fillMaxWidth(),
                onEnableSelection = {},
                onDeselectItem = {},
                onSelectItem = {},
                onItemClick = {}
            )
        }
    }
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
    onItemClick: (Download) -> Unit,
) {
    var isSelectionDisable by remember { mutableStateOf(true) }

    LaunchedEffect(itemSelection) {
        isSelectionDisable = itemSelection.isEmpty()
    }

    Column(modifier) {
        BannerNativeAd(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Shimmer()
        }

        if (downloaded.isEmpty()) {
            EmptyStateMessage(
                icon = Icons.Filled.VideoLibrary,
                title = "No Downloads Yet",
                message = "Your downloaded videos will appear here",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AnimatedVisibility(
                visible = !isSelectionDisable || downloaded.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SelectItemWidget(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    itemSelection = itemSelection,
                    totalItems = downloaded.size,
                    onSelectAllClick = onSelectAll,
                    onDeselectAllClick = onDeselectAllClick,
                    onDeleteClick = onDeleteClick
                )
            }

            DownloadItemList(
                downloads = downloaded,
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
}

@Composable
fun EmptyStateMessage(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadItemList(
    downloads: List<Download>,
    modifier: Modifier = Modifier,
    isSelectionDisable: Boolean = true,
    onItemClick: (Download) -> Unit,
    selection: List<Download> = emptyList(),
    onSelectItem: (Download) -> Unit,
    onDeselectItem: (Download) -> Unit,
    onEnableSelection: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = downloads,
            key = { it.id }
        ) { item ->
            val isSelected = isSelectionDisable.not() && selection.contains(item)

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .animateItemPlacement(tween(300)),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (isSelected) 4.dp else 2.dp
                ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DownloadItem(
                        item = item,
                        modifier = Modifier
                            .fillMaxWidth()
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
                            .padding(16.dp)
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectItemWidget(
    modifier: Modifier,
    itemSelection: List<Download>,
    totalItems: Int,
    onSelectAllClick: () -> Unit,
    onDeselectAllClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Selection info
            Text(
                text = "${itemSelection.size} of $totalItems selected",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Select All Button
            IconButton(
                onClick = onSelectAllClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SelectAll,
                    contentDescription = "Select All",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Deselect All Button (only shown when items are selected)
            AnimatedVisibility(visible = itemSelection.isNotEmpty()) {
                FilledTonalIconButton(
                    onClick = onDeselectAllClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Deselect,
                        contentDescription = "Deselect All"
                    )
                }
            }

            // Delete Button (only shown when items are selected)
            AnimatedVisibility(visible = itemSelection.isNotEmpty()) {
                FilledTonalIconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


